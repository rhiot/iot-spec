/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package io.rhiot.spec;

import java.util.concurrent.atomic.AtomicInteger;

public class Result {

    private AtomicInteger published = new AtomicInteger();
   	private AtomicInteger received = new AtomicInteger();

    public AtomicInteger getPublished() {
        return published;
    }

    public void setPublished(AtomicInteger published) {
        this.published = published;
    }

    public AtomicInteger getReceived() {
        return received;
    }

    public void setReceived(AtomicInteger received) {
        this.received = received;
    }

    public int received() {
        return received.incrementAndGet();
    }

    public int published() {
        return published.incrementAndGet();
    }

    @Override
    public String toString() {
        return "Result{" +
                "published=" + published +
                ", received=" + received +
                '}';
    }
}
