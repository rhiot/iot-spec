/*
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.spec.report;

import io.rhiot.spec.Driver;

import java.io.File;
import java.io.FileWriter;

public class CSVReport implements Report {

    FileWriter writer;

    public CSVReport(String fileName) throws Exception {
        File file = new File(fileName);
        writer = new FileWriter(file, true);
        if (!file.exists()) {
            writer.append("name,published,received" + System.lineSeparator());
        }
    }

    @Override
    public void print(Driver driver) throws Exception {
        writer.append(driver.getName());
        writer.append(",");
        writer.append(driver.getResult().getPublished().toString());
        writer.append(",");
        writer.append(driver.getResult().getReceived().toString());
        writer.append(System.lineSeparator());
    }

    @Override
    public void close() throws Exception {
        writer.flush();
        writer.close();
    }

}
