package com.cwj.express.common.config.ribbon;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author root
 */
@Configuration
@RibbonClients(defaultConfiguration = RibbonRuleConfig.class)
public class RibbonConfiguration {
}
