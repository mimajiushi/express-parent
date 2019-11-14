package com.cwj.express.auth.service.impl;

import com.baidu.aip.face.AipFace;
import com.cwj.express.auth.config.thirdapi.BaiduFaceConfig;
import com.cwj.express.auth.config.thirdapi.ThirdApiConfig;
import com.cwj.express.auth.service.FaceService;
import com.cwj.express.auth.service.SysUserService;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.ucenter.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cwj
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FaceServiceImpl implements FaceService {

    private final AipFace aipFace;
    private final SysUserService sysUserService;
    private final ThirdApiConfig thirdApiConfig;

    @Override
    public ResponseResult faceDetectByBase64(String image, boolean isQuality) {
        HashMap<String, String> options = new HashMap<>();

        StringBuffer sb = new StringBuffer("gender,face_type");
        if(isQuality) {
            sb.append(",quality");
        }
        options.put("face_field", sb.toString());
        options.put("max_face_num", "1");
        options.put("face_type", "LIVE");

        // 人脸检测
        JSONObject res = aipFace.detect(image, "BASE64", options);
        log.info("res: {}", res);
        // 校验错误
        Integer errorCode = (Integer)res.get("error_code");
        if(errorCode != 0) {
            log.info("错误码：{}，错误信息：{}", errorCode, res.get("error_msg"));
            return ResponseResult.FAIL(res.get("error_msg"));
        }

        // 取第一条记录
        JSONArray faceList = (JSONArray) ((JSONObject)res.get("result")).get("face_list");
        if(faceList.length() == 0) {
            return ResponseResult.FAIL(CommonCode.AUTH_NOT_MATCH_FACE);
        }
        JSONObject faceMap = (JSONObject)faceList.get(0);

        // 人脸置信度，0~1
        if(Double.valueOf(faceMap.get("face_probability").toString()) < 0.9) {
            return ResponseResult.FAIL(CommonCode.AUTH_NOT_DETECT_FACE);
        }

        // 判断真人、卡通
        String faceTpe = ((JSONObject) faceMap.get("face_type")).get("type").toString();
        if(!"human".equals(faceTpe)) {
            return ResponseResult.FAIL(CommonCode.AUTH_NOT_REAL_FACE);
        }

        // 质量检测(如果开启)
        if(isQuality) {
            ResponseResult responseResult = faceQuality(faceMap);
            if (responseResult.getCode() != ResponseResult.SUCCESS_CODE){
                return responseResult;
            }
        }

        // 返回数据
        Map<String, String> resultMap = new HashMap<>();
        // faceToken
        resultMap.put("face_token", (String)faceMap.get("face_token"));
        // 性别
        resultMap.put("gender", (String)((JSONObject) faceMap.get("gender")).get("type"));

        return ResponseResult.SUCCESS(resultMap);
    }

    @Override
    public ResponseResult faceRegistryByFaceToken(String faceToken, String userId) {
        return null;
    }

    @Override
    public ResponseResult faceSearchByBase64(String image) {
        BaiduFaceConfig baiduFaceConf = thirdApiConfig.getBaidu();

        HashMap<String, String> options = new HashMap<>();
        // 图片质量控制 NONE: 不进行控制 LOW:较低的质量要求 NORMAL: 一般的质量要求 HIGH: 较高的质量要求
        options.put("quality_control", "NORMAL");
        // 活体检测控制 NONE: 不进行控制 LOW:较低的活体要求(高通过率 低攻击拒绝率)
        // NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率) HIGH: 较高的活体要求(高攻击拒绝率 低通过率)
        options.put("liveness_control", "NORMAL");
        // 查找后返回的用户数量。返回相似度最高的几个用户，默认为1，最多返回20个。
        options.put("max_user_num", "1");

        // 人脸搜索
        JSONObject res = aipFace.search(image, "BASE64", baiduFaceConf.getGroupId(), options);
        // 校验错误
        Integer errorCode = (Integer)res.get("error_code");
        if(errorCode != 0) {
            log.info("错误码：{}，错误信息：{}", errorCode, res.get("error_msg"));
            return ResponseResult.FAIL((String)res.get("error_msg"));
        }
        /*
         {
          "face_token": "fid",
          "user_list": [
             {
                "group_id" : "test1",
                "user_id": "u333333",
                "user_info": "Test User",
                "score": 99.3
            }
          ]
        }
         */
        // 取第一条记录
        JSONArray userList = (JSONArray) ((JSONObject)res.get("result")).get("user_list");
        if(userList.length() == 0) {
            return ResponseResult.FAIL(CommonCode.AUTH_NOT_MATCH_FACE);
        }
        JSONObject userMap = (JSONObject)userList.get(0);

        // 人脸置信度校验
        Double score = Double.valueOf(userMap.get("score").toString());
        if(score < baiduFaceConf.getAcceptScore()) {
            return ResponseResult.FAIL(CommonCode.AUTH_NOT_ACCORD_WITH_MIN_REQUIREMENT);
        }

        String userId = userMap.get("user_id").toString();
        SysUser sysUser = sysUserService.getById(userId);
        // 用户存在于AIP，但是不存在于本地数据
        if(ObjectUtils.isEmpty(sysUser)) {
            return ResponseResult.FAIL(CommonCode.AUTH_NOT_MATCH_FACE);
        }

        return ResponseResult.SUCCESS(sysUser);
    }

    @Override
    public ResponseResult faceUpdateByFaceToken(String faceToken, String userId) {
        return null;
    }


    /**
     * 人脸质量判断、限制
     * 参考官方推荐 http://ai.baidu.com/docs#/Face-Java-SDK/ca2bad80
     */
    public ResponseResult faceQuality(JSONObject faceMap){
        // 人脸旋转
        JSONObject angel = (JSONObject) faceMap.get("angle");
        if(Double.valueOf(angel.get("yaw").toString()) > 20 ||
                Double.valueOf(angel.get("pitch").toString()) > 20 ||
                Double.valueOf(angel.get("roll").toString()) > 20) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_ANGEL_BAD);
        }

        JSONObject quality = (JSONObject) faceMap.get("quality");
        // 模糊度范围
        if(Double.valueOf(quality.get("blur").toString()) >= 0.7) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_BLUR_BAD);
        }
        // 光照范围
        if((Integer)quality.get("illumination") <= 40) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_ILLUMINATION_BAD);
        }
        // 人脸完整度
        if((Integer)quality.get("completeness") != 1) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_ILLUMINATION_BAD);
        }
        // 遮挡范围
        JSONObject occlusion = (JSONObject) quality.get("occlusion");
        if(Double.valueOf(occlusion.get("left_eye").toString()) > 0.6 ||
                Double.valueOf(occlusion.get("right_eye").toString()) > 0.6 ) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_EYE_OCCLUSION_BAD);
        }
        if(Double.valueOf(occlusion.get("nose").toString()) > 0.7) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_NOSE_OCCLUSION_BAD);
        }
        if(Double.valueOf(occlusion.get("mouth").toString()) > 0.7) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_MOUTH_OCCLUSION_BAD);
        }
        if(Double.valueOf(occlusion.get("left_cheek").toString()) > 0.8 ||
                Double.valueOf(occlusion.get("right_cheek").toString()) > 0.8 ) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_CHEEK_OCCLUSION_BAD);
        }
        if(Double.valueOf(occlusion.get("chin_contour").toString()) > 0.6) {
            return ResponseResult.FAIL(CommonCode.AUTH_FACE_CHIN_OCCLUSION_BAD);
        }
        return ResponseResult.SUCCESS();
    }
}
