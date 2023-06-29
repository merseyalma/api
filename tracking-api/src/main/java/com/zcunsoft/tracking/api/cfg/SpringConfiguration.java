package com.zcunsoft.tracking.api.cfg;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The Class SpringConfiguration.
 *
 * @author yuhao
 */
@Configuration
@EnableConfigurationProperties({ TrackingApiSetting.class })
public class SpringConfiguration {

}
