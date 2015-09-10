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
package lab.examples.zookeeper.demo;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ZooKeeperConnector implements InitializingBean, DisposableBean {

    private String hosts;
    private ZooKeeper zooKeeper;
    
    // To block any operation until ZooKeeper is connected. It's initialized
    // with count 1, that is, ZooKeeper connect state.
    private java.util.concurrent.CountDownLatch connectedSignal = new java.util.concurrent.CountDownLatch(1);

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        zooKeeper.close();        
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (hosts == null) {
            throw new IllegalArgumentException("No hosts set for the ZookeeperConnector. Zookeeper connection cannot be created.");
        }
        
        zooKeeper = new ZooKeeper(
                hosts,                      // ZooKeeper service hosts
                5000,                       // Session timeout in milliseconds                
                new Watcher() {             // Anonymous Watcher Object
                        @Override
                        public void process(WatchedEvent event) {
                        // release lock if ZooKeeper is connected.
                        if (event.getState() == KeeperState.SyncConnected) {
                            connectedSignal.countDown();
                        }
                        }
                    }
            );
        connectedSignal.await();
    }
    
    /**
     * @return the zooKeeper connection
     */
    public ZooKeeper getZooKeeper() {
        // Verify ZooKeeper's validity
        if (zooKeeper == null || !zooKeeper.getState().equals(States.CONNECTED)){
            throw new IllegalStateException ("ZooKeeper is not connected.");
        }
        return zooKeeper;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }    

}