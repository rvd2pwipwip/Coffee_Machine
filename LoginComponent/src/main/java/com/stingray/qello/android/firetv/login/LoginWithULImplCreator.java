/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.stingray.qello.android.firetv.login;


import com.stingray.qello.firetv.android.module.IImplCreator;
import com.stingray.qello.firetv.auth.IAuthentication;

/**
 * This lets modules follow the same protocol for creating an instance.
 */
public class LoginWithULImplCreator implements IImplCreator<IAuthentication> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IAuthentication createImpl() {

        return new LoginWithULAuthentication();
    }
}
