package com.alibaba.boot.nacos.context.event;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author yizhan
 */
public class NacosEventApplicationListener
		implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
	@Override
	public void onApplicationEvent(
			ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {

	}
}
