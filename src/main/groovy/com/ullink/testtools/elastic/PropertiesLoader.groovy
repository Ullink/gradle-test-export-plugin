/*************************************************************************
 * ULLINK CONFIDENTIAL INFORMATION
 * _______________________________
 *
 * All Rights Reserved.
 *
 * NOTICE: This file and its content are the property of Ullink. The
 * information included has been classified as Confidential and may
 * not be copied, modified, distributed, or otherwise disseminated, in
 * whole or part, without the express written permission of Ullink.
 ************************************************************************/
package com.ullink.testtools.elastic

class PropertiesLoader {

    private final Properties parameters = new Properties()

    PropertiesLoader(InputStream loadingClass) throws IOException {
        parameters.load(loadingClass)
    }

    Properties getParameters() {
        return parameters
    }
}
