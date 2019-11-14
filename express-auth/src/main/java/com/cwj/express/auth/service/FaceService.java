package com.cwj.express.auth.service;

import com.cwj.express.common.model.response.ResponseResult;

/**
 * 人脸识别业务接口
 * @author cwj
 */
public interface FaceService {
    /**
     * 人脸检测
     * @param isQuality 是否开启质量校验
     */
    ResponseResult faceDetectByBase64(String image, boolean isQuality);
    /**
     * 人脸注册
     * 注：先调用人脸检测，开启质量校验
     */
    ResponseResult faceRegistryByFaceToken(String faceToken, String userId);
    /**
     * 人脸搜索
     */
    ResponseResult faceSearchByBase64(String image);
    /**
     * 人脸更新
     * 注：先调用人脸检测，开启质量校验
     */
    ResponseResult faceUpdateByFaceToken(String faceToken, String userId);
}
