/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.boot.nacos.common;

import com.alibaba.nacos.api.exception.NacosException;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * An {@link AbstractFailureAnalyzer} that performs analysis of failures caused by a
 * {@link NacosException}
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class NacosFailureAnalyzer extends AbstractFailureAnalyzer<NacosException> {

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, NacosException cause) {
		StringBuilder description = new StringBuilder();
		switch (cause.getErrCode()) {
		case NacosException.CLIENT_INVALID_PARAM:
			description.append("client error: invalid param");
			break;
		case NacosException.CLIENT_OVER_THRESHOLD:
			description.append("client error: over client threshold");
			break;
		case NacosException.BAD_GATEWAY:
			description.append("server error: bad gateway");
			break;
		case NacosException.CONFLICT:
			description.append("server error: conflict");
			break;
		case NacosException.INVALID_PARAM:
			description.append("server error: invalid param");
			break;
		case NacosException.NO_RIGHT:
			description.append("server error: no right");
			break;
		case NacosException.OVER_THRESHOLD:
			description.append("server error: over threshold");
			break;
		case NacosException.SERVER_ERROR:
			description.append("server error: such as timeout");
			break;
		default:
			description.append("unknown reason");
		}
		description.append(". ").append(cause.getErrMsg());
		String action;
		if (description.toString().contains("client")) {
			action = "please check your client configuration";
		}
		else {
			action = "please check server status";
		}
		return new FailureAnalysis(description.toString(), action, cause);
	}

}
