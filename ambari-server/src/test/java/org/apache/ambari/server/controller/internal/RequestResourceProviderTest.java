/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ambari.server.controller.internal;

import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleStatus;
import org.apache.ambari.server.controller.ExecuteActionRequest;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.RequestStatusResponse;
import org.apache.ambari.server.controller.spi.NoSuchParentResourceException;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * RequestResourceProvider tests.
 */
public class RequestResourceProviderTest {
  @Test
  public void testCreateResources() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);

    // replay
    replay(managementController, response);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    // add the property map to a set for the request.  add more maps for multiple creates
    Set<Map<String, Object>> propertySet = new LinkedHashSet<Map<String, Object>>();

    Map<String, Object> properties = new LinkedHashMap<String, Object>();

    // add properties to the request map
    properties.put(RequestResourceProvider.REQUEST_ID_PROPERTY_ID, "Request100");

    propertySet.add(properties);

    // create the request
    Request request = PropertyHelper.getCreateRequest(propertySet, null);

    try {
      provider.createResources(request);
      Assert.fail("Expected an UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // expected
    }

    // verify
    verify(managementController, response);
  }

  @Test
  public void testGetResources() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    ActionManager actionManager = createNiceMock(ActionManager.class);
    HostRoleCommand hostRoleCommand = createNiceMock(HostRoleCommand.class);


    List<HostRoleCommand> hostRoleCommands = new LinkedList<HostRoleCommand>();
    hostRoleCommands.add(hostRoleCommand);

    org.apache.ambari.server.actionmanager.Request requestMock =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock.getCommands()).andReturn(hostRoleCommands).anyTimes();
    expect(requestMock.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock.getRequestId()).andReturn(100L).anyTimes();

    Capture<Collection<Long>> requestIdsCapture = new Capture<Collection<Long>>();

    // set expectations
    expect(managementController.getActionManager()).andReturn(actionManager);
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Collections.singletonList(requestMock)).anyTimes();
    expect(hostRoleCommand.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand.getStatus()).andReturn(HostRoleStatus.IN_PROGRESS);

    // replay
    replay(managementController, actionManager, hostRoleCommand, requestMock);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Set<String> propertyIds = new HashSet<String>();

    propertyIds.add(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID);

    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").
        toPredicate();
    Request request = PropertyHelper.getReadRequest(propertyIds);
    Set<Resource> resources = provider.getResources(request, predicate);

    Assert.assertEquals(1, resources.size());
    for (Resource resource : resources) {
      Assert.assertEquals(100L, (long) (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_ID_PROPERTY_ID));
      Assert.assertEquals("IN_PROGRESS", resource.getPropertyValue(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID));
    }

    // verify
    verify(managementController, actionManager, hostRoleCommand);
  }

  @Test
  public void testGetResourcesWithRequestSchedule() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    ActionManager actionManager = createNiceMock(ActionManager.class);
    HostRoleCommand hostRoleCommand = createNiceMock(HostRoleCommand.class);


    List<HostRoleCommand> hostRoleCommands = new LinkedList<HostRoleCommand>();
    hostRoleCommands.add(hostRoleCommand);

    org.apache.ambari.server.actionmanager.Request requestMock =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock.getCommands()).andReturn(hostRoleCommands).anyTimes();
    expect(requestMock.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock.getRequestId()).andReturn(100L).anyTimes();
    expect(requestMock.getRequestScheduleId()).andReturn(11L).anyTimes();


    Capture<Collection<Long>> requestIdsCapture = new Capture<Collection<Long>>();

    // set expectations
    expect(managementController.getActionManager()).andReturn(actionManager);
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Collections.singletonList(requestMock)).anyTimes();
    expect(hostRoleCommand.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand.getStatus()).andReturn(HostRoleStatus.IN_PROGRESS);

    // replay
    replay(managementController, actionManager, hostRoleCommand, requestMock);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Set<String> propertyIds = new HashSet<String>();

    propertyIds.add(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_SOURCE_SCHEDULE);


    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").
        toPredicate();
    Request request = PropertyHelper.getReadRequest(propertyIds);
    Set<Resource> resources = provider.getResources(request, predicate);

    Assert.assertEquals(1, resources.size());
    for (Resource resource : resources) {
      Assert.assertEquals(100L, (long) (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_ID_PROPERTY_ID));
      Assert.assertEquals("IN_PROGRESS", resource.getPropertyValue(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID));
      Assert.assertEquals(11L, (long) (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_SOURCE_SCHEDULE_ID));
    }

    // verify
    verify(managementController, actionManager, hostRoleCommand);
  }

  @Test
  public void testGetResourcesWithoutRequestSchedule() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    ActionManager actionManager = createNiceMock(ActionManager.class);
    HostRoleCommand hostRoleCommand = createNiceMock(HostRoleCommand.class);


    List<HostRoleCommand> hostRoleCommands = new LinkedList<HostRoleCommand>();
    hostRoleCommands.add(hostRoleCommand);

    org.apache.ambari.server.actionmanager.Request requestMock =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock.getCommands()).andReturn(hostRoleCommands).anyTimes();
    expect(requestMock.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock.getRequestId()).andReturn(100L).anyTimes();
    expect(requestMock.getRequestScheduleId()).andReturn(null).anyTimes();


    Capture<Collection<Long>> requestIdsCapture = new Capture<Collection<Long>>();

    // set expectations
    expect(managementController.getActionManager()).andReturn(actionManager);
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Collections.singletonList(requestMock)).anyTimes();
    expect(hostRoleCommand.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand.getStatus()).andReturn(HostRoleStatus.IN_PROGRESS);

    // replay
    replay(managementController, actionManager, hostRoleCommand, requestMock);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Set<String> propertyIds = new HashSet<String>();

    propertyIds.add(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_SOURCE_SCHEDULE);


    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").
        toPredicate();
    Request request = PropertyHelper.getReadRequest(propertyIds);
    Set<Resource> resources = provider.getResources(request, predicate);

    Assert.assertEquals(1, resources.size());
    for (Resource resource : resources) {
      Assert.assertEquals(100L, (long) (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_ID_PROPERTY_ID));
      Assert.assertEquals("IN_PROGRESS", resource.getPropertyValue(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID));
      Assert.assertEquals(null, resource.getPropertyValue(RequestResourceProvider.REQUEST_SOURCE_SCHEDULE));
    }

    // verify
    verify(managementController, actionManager, hostRoleCommand);
  }

  @Test
  public void testGetResourcesWithCluster() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    ActionManager actionManager = createNiceMock(ActionManager.class);
    HostRoleCommand hostRoleCommand = createNiceMock(HostRoleCommand.class);
    Clusters clusters = createNiceMock(Clusters.class);
    Cluster cluster = createNiceMock(Cluster.class);

    List<HostRoleCommand> hostRoleCommands = new LinkedList<HostRoleCommand>();
    hostRoleCommands.add(hostRoleCommand);

    org.apache.ambari.server.actionmanager.Request requestMock =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock.getCommands()).andReturn(hostRoleCommands).anyTimes();
    expect(requestMock.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock.getClusterName()).andReturn("c1").anyTimes();
    expect(requestMock.getRequestId()).andReturn(100L).anyTimes();

    Capture<Collection<Long>> requestIdsCapture = new Capture<Collection<Long>>();

    // set expectations
    expect(managementController.getActionManager()).andReturn(actionManager).anyTimes();
    expect(managementController.getClusters()).andReturn(clusters).anyTimes();
    expect(clusters.getCluster("c1")).andReturn(cluster).anyTimes();
    expect(clusters.getCluster("bad-cluster")).andThrow(new AmbariException("bad cluster!")).anyTimes();
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Collections.singletonList(requestMock));
    expect(hostRoleCommand.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand.getStatus()).andReturn(HostRoleStatus.IN_PROGRESS);

    // replay
    replay(managementController, actionManager, hostRoleCommand, clusters, cluster, requestMock);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Set<String> propertyIds = new HashSet<String>();

    propertyIds.add(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID);

    Predicate predicate = new PredicateBuilder().
        property(RequestResourceProvider.REQUEST_CLUSTER_NAME_PROPERTY_ID).equals("c1").and().
        property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").
        toPredicate();
    Request request = PropertyHelper.getReadRequest(propertyIds);
    Set<Resource> resources = provider.getResources(request, predicate);

    Assert.assertEquals(1, resources.size());
    for (Resource resource : resources) {
      Assert.assertEquals(100L, (long) (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_ID_PROPERTY_ID));
      Assert.assertEquals("IN_PROGRESS", resource.getPropertyValue(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID));
    }

    // try again with a bad cluster name
    predicate = new PredicateBuilder().
        property(RequestResourceProvider.REQUEST_CLUSTER_NAME_PROPERTY_ID).equals("bad-cluster").and().
        property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").
        toPredicate();
    try {
      provider.getResources(request, predicate);
    } catch (NoSuchParentResourceException e) {
      e.printStackTrace();
    }

    // verify
    verify(managementController, actionManager, hostRoleCommand, clusters, cluster);
  }

  @Test
  public void testGetResourcesOrPredicate() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    ActionManager actionManager = createNiceMock(ActionManager.class);
    HostRoleCommand hostRoleCommand = createNiceMock(HostRoleCommand.class);

    List<HostRoleCommand> hostRoleCommands = new LinkedList<HostRoleCommand>();
    hostRoleCommands.add(hostRoleCommand);

    org.apache.ambari.server.actionmanager.Request requestMock =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock.getCommands()).andReturn(hostRoleCommands).anyTimes();
    expect(requestMock.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock.getRequestId()).andReturn(100L).anyTimes();

    org.apache.ambari.server.actionmanager.Request requestMock1 =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock1.getCommands()).andReturn(hostRoleCommands).anyTimes();
    expect(requestMock1.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock1.getRequestId()).andReturn(101L).anyTimes();

    Capture<Collection<Long>> requestIdsCapture = new Capture<Collection<Long>>();

    // set expectations
    expect(managementController.getActionManager()).andReturn(actionManager).anyTimes();
    expect(actionManager.getRequests(capture(requestIdsCapture))).
        andReturn(Arrays.asList(requestMock, requestMock1)).anyTimes();
    expect(hostRoleCommand.getStatus()).andReturn(HostRoleStatus.IN_PROGRESS).anyTimes();

    // replay
    replay(managementController, actionManager, hostRoleCommand, requestMock, requestMock1);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Set<String> propertyIds = new HashSet<String>();

    propertyIds.add(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);

    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").
        or().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("101").
        toPredicate();
    Request request = PropertyHelper.getReadRequest(propertyIds);
    Set<Resource> resources = provider.getResources(request, predicate);

    Assert.assertEquals(2, resources.size());
    for (Resource resource : resources) {
      long id = (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
      Assert.assertTrue(id == 100L || id == 101L);
    }

    // verify
    verify(managementController, actionManager, hostRoleCommand);
  }

  @Test
  public void testGetResourcesCompleted() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    ActionManager actionManager = createNiceMock(ActionManager.class);
    HostRoleCommand hostRoleCommand0 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand1 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand2 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand3 = createNiceMock(HostRoleCommand.class);

    List<HostRoleCommand> hostRoleCommands0 = new LinkedList<HostRoleCommand>();
    hostRoleCommands0.add(hostRoleCommand0);
    hostRoleCommands0.add(hostRoleCommand1);

    List<HostRoleCommand> hostRoleCommands1 = new LinkedList<HostRoleCommand>();
    hostRoleCommands1.add(hostRoleCommand2);
    hostRoleCommands1.add(hostRoleCommand3);

    org.apache.ambari.server.actionmanager.Request requestMock0 =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock0.getCommands()).andReturn(hostRoleCommands0).anyTimes();
    expect(requestMock0.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock0.getRequestId()).andReturn(100L).anyTimes();

    org.apache.ambari.server.actionmanager.Request requestMock1 =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock1.getCommands()).andReturn(hostRoleCommands1).anyTimes();
    expect(requestMock1.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock1.getRequestId()).andReturn(101L).anyTimes();

    Capture<Collection<Long>> requestIdsCapture = new Capture<Collection<Long>>();

    // set expectations
    expect(managementController.getActionManager()).andReturn(actionManager).anyTimes();
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Arrays.asList(requestMock0));
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Arrays.asList(requestMock1));
    expect(hostRoleCommand0.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand1.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand2.getRequestId()).andReturn(101L).anyTimes();
    expect(hostRoleCommand3.getRequestId()).andReturn(101L).anyTimes();
    expect(hostRoleCommand0.getStatus()).andReturn(HostRoleStatus.COMPLETED).anyTimes();
    expect(hostRoleCommand1.getStatus()).andReturn(HostRoleStatus.COMPLETED).anyTimes();
    expect(hostRoleCommand2.getStatus()).andReturn(HostRoleStatus.COMPLETED).anyTimes();
    expect(hostRoleCommand3.getStatus()).andReturn(HostRoleStatus.COMPLETED).anyTimes();

    // replay
    replay(managementController, actionManager, hostRoleCommand0, hostRoleCommand1, hostRoleCommand2, hostRoleCommand3,
        requestMock0, requestMock1);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Set<String> propertyIds = new HashSet<String>();

    propertyIds.add(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_COMPLETED_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_FAILED_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_PROGRESS_PERCENT_ID);

    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").or().
        property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("101").toPredicate();
    Request request = PropertyHelper.getReadRequest(propertyIds);
    Set<Resource> resources = provider.getResources(request, predicate);

    Assert.assertEquals(2, resources.size());
    for (Resource resource : resources) {
      long id = (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
      Assert.assertTrue(id == 100L || id == 101L);
      Assert.assertEquals("COMPLETED", resource.getPropertyValue(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID));
      Assert.assertEquals(2, resource.getPropertyValue(RequestResourceProvider.REQUEST_TASK_CNT_ID));
      Assert.assertEquals(0, resource.getPropertyValue(RequestResourceProvider.REQUEST_FAILED_TASK_CNT_ID));
      Assert.assertEquals(2, resource.getPropertyValue(RequestResourceProvider.REQUEST_COMPLETED_TASK_CNT_ID));

      Assert.assertEquals(100.0, resource.getPropertyValue(RequestResourceProvider.REQUEST_PROGRESS_PERCENT_ID));
    }

    // verify
    verify(managementController, actionManager, hostRoleCommand0, hostRoleCommand1, hostRoleCommand2, hostRoleCommand3);
  }

  @Test
  public void testGetResourcesInProgress() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    ActionManager actionManager = createNiceMock(ActionManager.class);
    HostRoleCommand hostRoleCommand0 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand1 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand2 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand3 = createNiceMock(HostRoleCommand.class);

    List<HostRoleCommand> hostRoleCommands0 = new LinkedList<HostRoleCommand>();
    hostRoleCommands0.add(hostRoleCommand0);
    hostRoleCommands0.add(hostRoleCommand1);

    List<HostRoleCommand> hostRoleCommands1 = new LinkedList<HostRoleCommand>();
    hostRoleCommands1.add(hostRoleCommand2);
    hostRoleCommands1.add(hostRoleCommand3);

    org.apache.ambari.server.actionmanager.Request requestMock0 =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock0.getCommands()).andReturn(hostRoleCommands0).anyTimes();
    expect(requestMock0.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock0.getRequestId()).andReturn(100L).anyTimes();

    org.apache.ambari.server.actionmanager.Request requestMock1 =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock1.getCommands()).andReturn(hostRoleCommands1).anyTimes();
    expect(requestMock1.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock1.getRequestId()).andReturn(101L).anyTimes();

    Capture<Collection<Long>> requestIdsCapture = new Capture<Collection<Long>>();

    // set expectations
    expect(managementController.getActionManager()).andReturn(actionManager).anyTimes();
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Arrays.asList(requestMock0));
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Arrays.asList(requestMock1));
    expect(hostRoleCommand0.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand1.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand2.getRequestId()).andReturn(101L).anyTimes();
    expect(hostRoleCommand3.getRequestId()).andReturn(101L).anyTimes();
    expect(hostRoleCommand0.getStatus()).andReturn(HostRoleStatus.IN_PROGRESS).anyTimes();
    expect(hostRoleCommand1.getStatus()).andReturn(HostRoleStatus.PENDING).anyTimes();
    expect(hostRoleCommand2.getStatus()).andReturn(HostRoleStatus.IN_PROGRESS).anyTimes();
    expect(hostRoleCommand3.getStatus()).andReturn(HostRoleStatus.QUEUED).anyTimes();

    // replay
    replay(managementController, actionManager, hostRoleCommand0, hostRoleCommand1, hostRoleCommand2, hostRoleCommand3,
        requestMock0, requestMock1);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Set<String> propertyIds = new HashSet<String>();

    propertyIds.add(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_COMPLETED_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_FAILED_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_QUEUED_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_PROGRESS_PERCENT_ID);

    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").or().
        property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("101").toPredicate();
    Request request = PropertyHelper.getReadRequest(propertyIds);
    Set<Resource> resources = provider.getResources(request, predicate);

    Assert.assertEquals(2, resources.size());
    for (Resource resource : resources) {
      long id = (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
      Assert.assertTrue(id == 100L || id == 101L);
      Assert.assertEquals("IN_PROGRESS", resource.getPropertyValue(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID));
      Assert.assertEquals(2, resource.getPropertyValue(RequestResourceProvider.REQUEST_TASK_CNT_ID));
      Assert.assertEquals(0, resource.getPropertyValue(RequestResourceProvider.REQUEST_FAILED_TASK_CNT_ID));

      if (id == 100L) {
        Assert.assertEquals(0, resource.getPropertyValue(RequestResourceProvider.REQUEST_QUEUED_TASK_CNT_ID));
        int progressPercent = ((Double) resource.getPropertyValue(RequestResourceProvider.REQUEST_PROGRESS_PERCENT_ID)).intValue();
        Assert.assertEquals(17, progressPercent);
      } else {
        Assert.assertEquals(1, resource.getPropertyValue(RequestResourceProvider.REQUEST_QUEUED_TASK_CNT_ID));
        int progressPercent = ((Double) resource.getPropertyValue(RequestResourceProvider.REQUEST_PROGRESS_PERCENT_ID)).intValue();
        Assert.assertEquals(21, progressPercent);
      }
      Assert.assertEquals(0, resource.getPropertyValue(RequestResourceProvider.REQUEST_COMPLETED_TASK_CNT_ID));
    }

    // verify
    verify(managementController, actionManager, hostRoleCommand0, hostRoleCommand1, hostRoleCommand2, hostRoleCommand3);
  }

  @Test
  public void testGetResourcesFailed() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    ActionManager actionManager = createNiceMock(ActionManager.class);
    HostRoleCommand hostRoleCommand0 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand1 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand2 = createNiceMock(HostRoleCommand.class);
    HostRoleCommand hostRoleCommand3 = createNiceMock(HostRoleCommand.class);

    List<HostRoleCommand> hostRoleCommands0 = new LinkedList<HostRoleCommand>();
    hostRoleCommands0.add(hostRoleCommand0);
    hostRoleCommands0.add(hostRoleCommand1);

    List<HostRoleCommand> hostRoleCommands1 = new LinkedList<HostRoleCommand>();
    hostRoleCommands1.add(hostRoleCommand2);
    hostRoleCommands1.add(hostRoleCommand3);

    org.apache.ambari.server.actionmanager.Request requestMock0 =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock0.getCommands()).andReturn(hostRoleCommands0).anyTimes();
    expect(requestMock0.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock0.getRequestId()).andReturn(100L).anyTimes();

    org.apache.ambari.server.actionmanager.Request requestMock1 =
        createNiceMock(org.apache.ambari.server.actionmanager.Request.class);
    expect(requestMock1.getCommands()).andReturn(hostRoleCommands1).anyTimes();
    expect(requestMock1.getRequestContext()).andReturn("this is a context").anyTimes();
    expect(requestMock1.getRequestId()).andReturn(101L).anyTimes();

    Capture<Collection<Long>> requestIdsCapture = new Capture<Collection<Long>>();

    // set expectations
    expect(managementController.getActionManager()).andReturn(actionManager).anyTimes();
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Arrays.asList(requestMock0));
    expect(actionManager.getRequests(capture(requestIdsCapture))).andReturn(Arrays.asList(requestMock1));
    expect(hostRoleCommand0.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand1.getRequestId()).andReturn(100L).anyTimes();
    expect(hostRoleCommand2.getRequestId()).andReturn(101L).anyTimes();
    expect(hostRoleCommand3.getRequestId()).andReturn(101L).anyTimes();
    expect(hostRoleCommand0.getStatus()).andReturn(HostRoleStatus.FAILED).anyTimes();
    expect(hostRoleCommand1.getStatus()).andReturn(HostRoleStatus.COMPLETED).anyTimes();
    expect(hostRoleCommand2.getStatus()).andReturn(HostRoleStatus.ABORTED).anyTimes();
    expect(hostRoleCommand3.getStatus()).andReturn(HostRoleStatus.TIMEDOUT).anyTimes();

    // replay
    replay(managementController, actionManager, hostRoleCommand0, hostRoleCommand1, hostRoleCommand2, hostRoleCommand3,
        requestMock0, requestMock1);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Set<String> propertyIds = new HashSet<String>();

    propertyIds.add(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_COMPLETED_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_FAILED_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_ABORTED_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_TIMED_OUT_TASK_CNT_ID);
    propertyIds.add(RequestResourceProvider.REQUEST_PROGRESS_PERCENT_ID);

    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("100").or().
        property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).equals("101").toPredicate();
    Request request = PropertyHelper.getReadRequest(propertyIds);
    Set<Resource> resources = provider.getResources(request, predicate);

    Assert.assertEquals(2, resources.size());
    for (Resource resource : resources) {
      long id = (Long) resource.getPropertyValue(RequestResourceProvider.REQUEST_ID_PROPERTY_ID);
      Assert.assertTrue(id == 100L || id == 101L);
      Assert.assertEquals(2, resource.getPropertyValue(RequestResourceProvider.REQUEST_TASK_CNT_ID));
      if (id == 100L) {
        Assert.assertEquals("FAILED", resource.getPropertyValue(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID));
        Assert.assertEquals(1, resource.getPropertyValue(RequestResourceProvider.REQUEST_FAILED_TASK_CNT_ID));
        Assert.assertEquals(0, resource.getPropertyValue(RequestResourceProvider.REQUEST_ABORTED_TASK_CNT_ID));
        Assert.assertEquals(0, resource.getPropertyValue(RequestResourceProvider.REQUEST_TIMED_OUT_TASK_CNT_ID));
      } else {
        Assert.assertEquals("ABORTED", resource.getPropertyValue(RequestResourceProvider.REQUEST_STATUS_PROPERTY_ID));
        Assert.assertEquals(0, resource.getPropertyValue(RequestResourceProvider.REQUEST_FAILED_TASK_CNT_ID));
        Assert.assertEquals(1, resource.getPropertyValue(RequestResourceProvider.REQUEST_ABORTED_TASK_CNT_ID));
        Assert.assertEquals(1, resource.getPropertyValue(RequestResourceProvider.REQUEST_TIMED_OUT_TASK_CNT_ID));
      }
      Assert.assertEquals(2, resource.getPropertyValue(RequestResourceProvider.REQUEST_COMPLETED_TASK_CNT_ID));
      Assert.assertEquals(100.0, resource.getPropertyValue(RequestResourceProvider.REQUEST_PROGRESS_PERCENT_ID));
    }

    // verify
    verify(managementController, actionManager, hostRoleCommand0, hostRoleCommand1, hostRoleCommand2, hostRoleCommand3);
  }

  @Test
  public void testUpdateResources() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);

    // replay
    replay(managementController, response);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    // add the property map to a set for the request.
    Map<String, Object> properties = new LinkedHashMap<String, Object>();

    // create the request
    Request request = PropertyHelper.getUpdateRequest(properties, null);

    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).
        equals("Request100").toPredicate();

    try {
      provider.updateResources(request, predicate);
      Assert.fail("Expected an UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // expected
    }

    // verify
    verify(managementController, response);
  }

  @Test
  public void testDeleteResources() throws Exception {
    Resource.Type type = Resource.Type.Request;

    AmbariManagementController managementController = createMock(AmbariManagementController.class);

    // replay
    replay(managementController);

    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    Predicate predicate = new PredicateBuilder().property(RequestResourceProvider.REQUEST_ID_PROPERTY_ID).
        equals("Request100").toPredicate();
    try {
      provider.deleteResources(predicate);
      Assert.fail("Expected an UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // expected
    }

    // verify
    verify(managementController);
  }

  @Test
  public void testCreateResourcesForCommands() throws Exception {
    Resource.Type type = Resource.Type.Request;

    Capture<ExecuteActionRequest> actionRequest = new Capture<ExecuteActionRequest>();
    Capture<HashMap<String, String>> propertyMap = new Capture<HashMap<String, String>>();

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);

    expect(managementController.createAction(capture(actionRequest), capture(propertyMap)))
        .andReturn(response).anyTimes();

    // replay
    replay(managementController);

    // add the property map to a set for the request.  add more maps for multiple creates
    Set<Map<String, Object>> propertySet = new LinkedHashSet<Map<String, Object>>();

    Map<String, Object> properties = new LinkedHashMap<String, Object>();

    properties.put(RequestResourceProvider.REQUEST_CLUSTER_NAME_PROPERTY_ID, "c1");

    propertySet.add(properties);

    Map<String, String> requestInfoProperties = new HashMap<String, String>();
    requestInfoProperties.put(RequestResourceProvider.SERVICE_NAME_ID, "HDFS");
    requestInfoProperties.put(RequestResourceProvider.COMMAND_ID, "HDFS_SERVICE_CHECK");

    // create the request
    Request request = PropertyHelper.getCreateRequest(propertySet, requestInfoProperties);
    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);
    provider.createResources(request);
    Assert.assertTrue(actionRequest.hasCaptured());
    Assert.assertTrue(actionRequest.getValue().isCommand());
    Assert.assertEquals(null, actionRequest.getValue().getActionName());
    Assert.assertEquals("HDFS_SERVICE_CHECK", actionRequest.getValue().getCommandName());
    Assert.assertEquals("HDFS", actionRequest.getValue().getServiceName());
    Assert.assertEquals(null, actionRequest.getValue().getComponentName());
    Assert.assertNotNull(actionRequest.getValue().getHosts());
    Assert.assertEquals(0, actionRequest.getValue().getHosts().size());
    Assert.assertEquals(0, actionRequest.getValue().getParameters().size());
  }

  @Test
  public void testCreateResourcesForCommandsWithParams() throws Exception {
    Resource.Type type = Resource.Type.Request;

    Capture<ExecuteActionRequest> actionRequest = new Capture<ExecuteActionRequest>();
    Capture<HashMap<String, String>> propertyMap = new Capture<HashMap<String, String>>();

    AmbariManagementController managementController = createMock(AmbariManagementController.class);
    RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);

    expect(managementController.createAction(capture(actionRequest), capture(propertyMap)))
        .andReturn(response).anyTimes();

    // replay
    replay(managementController);

    // add the property map to a set for the request.  add more maps for multiple creates
    Set<Map<String, Object>> propertySet = new LinkedHashSet<Map<String, Object>>();

    Map<String, Object> properties = new LinkedHashMap<String, Object>();

    properties.put(RequestResourceProvider.REQUEST_CLUSTER_NAME_PROPERTY_ID, "c1");

    propertySet.add(properties);

    Map<String, String> requestInfoProperties = new HashMap<String, String>();
    requestInfoProperties.put(RequestResourceProvider.SERVICE_NAME_ID, "HDFS");
    requestInfoProperties.put("/parameters/param1", "value1");
    requestInfoProperties.put("/parameters/param2", "value2");
    requestInfoProperties.put(RequestResourceProvider.HOSTS_ID, "host1 ,host2, host3 ");

    String[] expectedHosts = new String[]{"host1", "host2", "host3"};
    Map<String, String> expectedParams = new HashMap<String, String>() {{
      put("param1", "value1");
      put("param2", "value2");
    }};

    // create the request
    Request request = PropertyHelper.getCreateRequest(propertySet, requestInfoProperties);
    ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(
        type,
        PropertyHelper.getPropertyIds(type),
        PropertyHelper.getKeyPropertyIds(type),
        managementController);

    // Neither action nor commands are specified
    try {
      provider.createResources(request);
    } catch (UnsupportedOperationException ex) {
      Assert.assertTrue(ex.getMessage().contains("Either command or action must be specified"));
    }

    // Both action and command are specified
    requestInfoProperties.put(RequestResourceProvider.COMMAND_ID, "HDFS_SERVICE_CHECK");
    requestInfoProperties.put(RequestResourceProvider.ACTION_ID, "a1");
    try {
      provider.createResources(request);
    } catch (UnsupportedOperationException ex) {
      Assert.assertTrue(ex.getMessage().contains("Both command and action cannot be specified"));
    }
    requestInfoProperties.remove(RequestResourceProvider.ACTION_ID);

    provider.createResources(request);
    Assert.assertTrue(actionRequest.hasCaptured());
    Assert.assertTrue(actionRequest.getValue().isCommand());
    Assert.assertEquals(null, actionRequest.getValue().getActionName());
    Assert.assertEquals("HDFS_SERVICE_CHECK", actionRequest.getValue().getCommandName());
    Assert.assertEquals("HDFS", actionRequest.getValue().getServiceName());
    Assert.assertEquals(null, actionRequest.getValue().getComponentName());
    Assert.assertEquals(3, actionRequest.getValue().getHosts().size());
    Assert.assertArrayEquals(expectedHosts, actionRequest.getValue().getHosts().toArray());
    Assert.assertEquals(2, actionRequest.getValue().getParameters().size());
    for(String key : expectedParams.keySet()) {
      Assert.assertEquals(expectedParams.get(key), actionRequest.getValue().getParameters().get(key));
    }
  }
}
