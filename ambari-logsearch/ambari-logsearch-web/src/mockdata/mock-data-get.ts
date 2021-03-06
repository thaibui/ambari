/**
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

import * as moment from 'moment';
import {Moment} from "moment";

import {
  clusters,
  hosts,
  services,
  users,
  components,
  levels,
  ucFirst,
  getRandomInt,
  getRandomElement,
  generatePath,
  generateServiceLog,
  generateAuditLog
} from './mock-data-common';

const currentTime: Moment = moment();


export const mockDataGet = {
  'login': {},
  'logout': {},

  'api/v1/audit/logs': function (query) {
    let list = [];
    let params = query.rawParams.split('&').reduce((currentObj, param) => {
      let [key, value] = param.split('=');
      switch (key) {
        case 'page':
        case 'pageSize':
        case 'startIndex':
          value = parseInt(value);
          break;
        case 'from':
        case 'to':
          value = decodeURIComponent(value);
          value = moment(value);
          break;
        case 'userList':
          value = decodeURIComponent(value).split(',');
          break;
      }
      currentObj[key] = value;
      return currentObj;
    }, {});
    const pageSize = params.pageSize || 50;
    const intervalSteps = params.to.diff(params.from) / pageSize;
    const startTime = params.from.valueOf();
    for (let i = 0; i < pageSize; i += 1) {
      let defaults: {[key:string]: any} = {logtime: startTime + (i * intervalSteps)};
      list.push(generateAuditLog(defaults));
    }
    return {
      'startIndex': params.startIndex,
      'pageSize': pageSize,
      'totalCount': 10 * pageSize,
      'resultSize': 10 * pageSize,
      'sortType': params.sortType,
      'sortBy': params.sortBy,
      'queryTimeMS': 1518013198573,
      'logList': list
    };
  },
  'api/v1/audit/logs/bargraph': {
    graphData: [{
      dataCount: [
        {
          name: currentTime.toISOString(),
          value: '75'
        },
        {
          name: currentTime.clone().subtract(20, 'm').toISOString(),
          value: '100'
        },
        {
          name: currentTime.clone().subtract(40, 'm').toISOString(),
          value: '75'
        },
        {
          name: currentTime.clone().subtract(1, 'h').toISOString(),
          value: '50'
        }
      ],
      name: 'AMBARI'
    }, {
      dataCount: [
        {
          name: currentTime.toISOString(),
          value: '150'
        },
        {
          name: currentTime.clone().subtract(20, 'm').toISOString(),
          value: '50'
        },
        {
          name: currentTime.clone().subtract(40, 'm').toISOString(),
          value: '75'
        },
        {
          name: currentTime.clone().subtract(1, 'h').toISOString(),
          value: '100'
        }
      ],
      name: 'HDFS'
    }
  ]},
  'api/v1/audit/logs/components': {
    'groups': {},
    'metadata': components.map(comp => {
      return {
        name: comp,
        label: comp.split('_').map(ucFirst).join(' '),
        group: null
      }
    })
  },
  'api/v1/audit/logs/resources/\\d': function (query) {
    let graphData = users.map((user:string) => {
      return {
        name: user,
        dataCount: services.map((service:string) => {
          return {
            name: service,
            value: getRandomInt(1000)
          }
        })
      };
    });
    return {
      graphData: graphData
    }
  },
  'api/v1/audit/logs/schema/fields': {
    'defaults': [
      {
        'name': 'logType',
        'label': 'Log Type',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'cluster',
        'label': 'Cluster',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'reason',
        'label': 'Reason',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'agent',
        'label': 'Agent',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'access',
        'label': 'Access Type',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'dst',
        'label': 'DST',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'perm',
        'label': 'Perm',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'event_count',
        'label': 'Event Count',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'repo',
        'label': 'Repo',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'sess',
        'label': 'Session',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'reqUser',
        'label': 'User',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'task_id',
        'label': 'Task Id',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'type',
        'label': 'Type',
        'filterable': true,
        'visible': true
      },
      {
        'name': 'reqData',
        'label': 'Req Data',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'result',
        'label': 'Result',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'path',
        'label': 'Path',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'file',
        'label': 'File',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'ugi',
        'label': 'UGI',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'case_id',
        'label': 'Case Id',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'host',
        'label': 'Host',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'action',
        'label': 'Action',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'log_message',
        'label': 'Log Message',
        'filterable': true,
        'visible': true
      },
      {
        'name': 'agentHost',
        'label': 'Agent Host',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'id',
        'label': 'Id',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'logger_name',
        'label': 'Logger Name',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'text',
        'label': 'Text',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'authType',
        'label': 'Auth Type',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'logfile_line_number',
        'label': 'Logfile Line Number',
        'filterable': true,
        'visible': true
      },
      {
        'name': 'policy',
        'label': 'Policy',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'cliIP',
        'label': 'Client Ip',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'level',
        'label': 'Level',
        'filterable': true,
        'visible': true
      },
      {
        'name': 'resource',
        'label': 'Resource',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'resType',
        'label': 'Res Type',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'ip',
        'label': 'IP',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'evtTime',
        'label': 'Event Time',
        'filterable': true,
        'visible': true
      },
      {
        'name': 'req_self_id',
        'label': 'Req Self Id',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'repoType',
        'label': 'Repo Type',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'req_caller_id',
        'label': 'Req Caller Id',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'enforcer',
        'label': 'Access Enforcer',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'bundle_id',
        'label': 'Bundle Id',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'cliType',
        'label': 'Client Type',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'reqContext',
        'label': 'Req Context',
        'filterable': true,
        'visible': false
      },
      {
        'name': 'proxyUsers',
        'label': 'Proxy Users',
        'filterable': true,
        'visible': false
      }
    ],
    'overrides': {
      'ambari': [
        {
          'name': 'logType',
          'label': 'Log Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cluster',
          'label': 'Cluster',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reason',
          'label': 'Reason',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'agent',
          'label': 'Agent',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'access',
          'label': 'Access',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'dst',
          'label': 'Dst',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'perm',
          'label': 'Perm',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'event_count',
          'label': 'Event Count',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'repo',
          'label': 'Repo',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'sess',
          'label': 'Sess',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqUser',
          'label': 'Req User',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'task_id',
          'label': 'Task Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'type',
          'label': 'Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqData',
          'label': 'Req Data',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'result',
          'label': 'Result',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'path',
          'label': 'Path',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'file',
          'label': 'File',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'ugi',
          'label': 'Ugi',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'case_id',
          'label': 'Case Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'host',
          'label': 'Host',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'action',
          'label': 'Action',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'log_message',
          'label': 'Log Message',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'agentHost',
          'label': 'Agent Host',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'id',
          'label': 'Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'logger_name',
          'label': 'Logger Name',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'text',
          'label': 'Text',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'authType',
          'label': 'Auth Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'logfile_line_number',
          'label': 'Logfile Line Number',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'policy',
          'label': 'Policy',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cliIP',
          'label': 'Cli I P',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'level',
          'label': 'Level',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'resource',
          'label': 'Resource',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'resType',
          'label': 'Res Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'ip',
          'label': 'Ip',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'evtTime',
          'label': 'Evt Time',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'req_self_id',
          'label': 'Req Self Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'repoType',
          'label': 'Repo Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'req_caller_id',
          'label': 'Req Caller Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'enforcer',
          'label': 'Enforcer',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'bundle_id',
          'label': 'Bundle Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cliType',
          'label': 'Cli Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqContext',
          'label': 'Req Context',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'proxyUsers',
          'label': 'Proxy Users',
          'filterable': true,
          'visible': false
        }
      ],
      'RangerAudit': [
        {
          'name': 'logType',
          'label': 'Log Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cluster',
          'label': 'Cluster',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reason',
          'label': 'Reason',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'agent',
          'label': 'Agent',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'access',
          'label': 'Access',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'dst',
          'label': 'Dst',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'perm',
          'label': 'Perm',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'event_count',
          'label': 'Event Count',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'repo',
          'label': 'Repo',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'sess',
          'label': 'Sess',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqUser',
          'label': 'Req User',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'task_id',
          'label': 'Task Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'type',
          'label': 'Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqData',
          'label': 'Req Data',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'result',
          'label': 'Result',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'path',
          'label': 'Path',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'file',
          'label': 'File',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'ugi',
          'label': 'Ugi',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'case_id',
          'label': 'Case Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'host',
          'label': 'Host',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'action',
          'label': 'Action',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'log_message',
          'label': 'Log Message',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'agentHost',
          'label': 'Agent Host',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'id',
          'label': 'Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'logger_name',
          'label': 'Logger Name',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'text',
          'label': 'Text',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'authType',
          'label': 'Auth Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'logfile_line_number',
          'label': 'Logfile Line Number',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'policy',
          'label': 'Policy',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cliIP',
          'label': 'Cli I P',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'level',
          'label': 'Level',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'resource',
          'label': 'Resource',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'resType',
          'label': 'Res Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'ip',
          'label': 'Ip',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'evtTime',
          'label': 'Evt Time',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'req_self_id',
          'label': 'Req Self Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'repoType',
          'label': 'Repo Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'req_caller_id',
          'label': 'Req Caller Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'enforcer',
          'label': 'Enforcer',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'bundle_id',
          'label': 'Bundle Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cliType',
          'label': 'Cli Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqContext',
          'label': 'Req Context',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'proxyUsers',
          'label': 'Proxy Users',
          'filterable': true,
          'visible': false
        }
      ],
      'hdfs': [
        {
          'name': 'logType',
          'label': 'Log Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cluster',
          'label': 'Cluster',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reason',
          'label': 'Reason',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'agent',
          'label': 'Agent',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'access',
          'label': 'Access',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'dst',
          'label': 'Dst',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'perm',
          'label': 'Perm',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'event_count',
          'label': 'Event Count',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'repo',
          'label': 'Repo',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'sess',
          'label': 'Sess',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqUser',
          'label': 'Req User',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'task_id',
          'label': 'Task Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'type',
          'label': 'Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqData',
          'label': 'Req Data',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'result',
          'label': 'Result',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'path',
          'label': 'Path',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'file',
          'label': 'File',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'ugi',
          'label': 'Ugi',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'case_id',
          'label': 'Case Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'host',
          'label': 'Host',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'action',
          'label': 'Action',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'log_message',
          'label': 'Log Message',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'agentHost',
          'label': 'Agent Host',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'id',
          'label': 'Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'logger_name',
          'label': 'Logger Name',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'text',
          'label': 'Text',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'authType',
          'label': 'Auth Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'logfile_line_number',
          'label': 'Logfile Line Number',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'policy',
          'label': 'Policy',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cliIP',
          'label': 'Cli I P',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'level',
          'label': 'Level',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'resource',
          'label': 'Resource',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'resType',
          'label': 'Res Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'ip',
          'label': 'Ip',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'evtTime',
          'label': 'Evt Time',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'req_self_id',
          'label': 'Req Self Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'repoType',
          'label': 'Repo Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'req_caller_id',
          'label': 'Req Caller Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'enforcer',
          'label': 'Enforcer',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'bundle_id',
          'label': 'Bundle Id',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'cliType',
          'label': 'Cli Type',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'reqContext',
          'label': 'Req Context',
          'filterable': true,
          'visible': false
        },
        {
          'name': 'proxyUsers',
          'label': 'Proxy Users',
          'filterable': true,
          'visible': false
        }
      ]
    }
  },
  'api/v1/audit/logs/serviceload': {
    graphData: [
      {
        dataCount: [
          {
            name: 'n4',
            value: 1
          },
          {
            name: 'n5',
            value: 2
          }
        ],
        name: 'graph2'
      },
      {
        dataCount: [
          {
            name: 'n6',
            value: 10
          },
          {
            name: 'n7',
            value: 20
          }
        ],
        name: 'graph3'
      }
    ]
  },

  'api/v1/public/config': {},

  'api/v1/service/logs': function (query) {
    let list = [];
    let params = query.rawParams.split('&').reduce((currentObj, param) => {
      let [key, value] = param.split('=');
      switch (key) {
        case 'page':
        case 'pageSize':
        case 'startIndex':
          value = parseInt(value);
          break;
        case 'from':
        case 'to':
          value = decodeURIComponent(value);
          value = moment(value);
          break;
        case 'mustBe':
        case 'hostList':
        case 'level':
          value = decodeURIComponent(value).split(',');
          break;
      }
      currentObj[key] = value;
      return currentObj;
    }, {});
    const pageSize = params.pageSize || 50;
    const intervalSteps = params.to.diff(params.from) / pageSize;
    const startTime = params.from.valueOf();

    for (let i = 0; i < pageSize; i += 1) {
      let defaults: {[key:string]: any} = {logtime: startTime + (i * intervalSteps)};
      if (params.mustBe) {
        defaults.type = getRandomElement(params.mustBe);
      }
      if (params.hostList) {
        defaults.host = getRandomElement(params.hostList);
      }
      if (params.level) {
        defaults.level = getRandomElement(params.level);
      }
      list.push(generateServiceLog(defaults));
    }
    return {
      'startIndex': params.startIndex,
      'pageSize': pageSize,
      'totalCount': 10 * pageSize,
      'resultSize': 10 * pageSize,
      'sortType': params.sortType,
      'sortBy': params.sortBy,
      'queryTimeMS': 1518013198573,
      'logList': list
    };
  },
  'api/v1/service/logs/logList': (query) => {
    let list = [];
    let params = query.rawParams.split('&').reduce((currentObj, param) => {
      let [key, value] = param.split('=');
      switch (key) {
        case 'page':
        case 'pageSize':
        case 'startIndex':
          value = parseInt(value);
          break;
        case 'from':
        case 'to':
          value = decodeURIComponent(value);
          value = moment(value);
          break;
        case 'mustBe':
        case 'hostList':
        case 'level':
          value = decodeURIComponent(value).split(',');
          break;
      }
      currentObj[key] = value;
      return currentObj;
    }, {});
    const pageSize = params.pageSize || 50;
    const intervalSteps = params.to.diff(params.from) / pageSize;
    const startTime = params.from.valueOf();

    for (let i = 0; i < pageSize; i += 1) {
      let defaults: {[key:string]: any} = {
        logtime: startTime + (i * intervalSteps),
        event_dur_ms: getRandomInt(1000)
      };
      if (params.mustBe) {
        defaults.type = getRandomElement(params.mustBe);
      }
      if (params.hostList) {
        defaults.host = getRandomElement(params.hostList);
      }
      if (params.level) {
        defaults.level = getRandomElement(params.level);
      }
      list.push(generateServiceLog(defaults));
    }
    return list;
  },
  'api/v1/service/logs/aggregated': {
    graphData: [
      {
        name: 'n0',
        count: 100,
        dataList: [
          {
            name: 'n1',
            count: 50,
            dataList: null
          },
          {
            name: 'n2',
            count: 200,
            dataList: null
          }
        ]
      },
      {
        name: 'n3',
        count: 10,
        dataList: [
          {
            name: 'n4',
            count: 5,
            dataList: null
          },
          {
            name: 'n5',
            count: 20,
            dataList: null
          }
        ]
      }
    ]
  },
  'api/v1/service/logs/components': {
    'groups': {},
    'metadata': components.map(comp => {
      return {
        name: comp,
        label: comp.split('_').map(ucFirst).join(' '),
        group: null
      }
    })
  },
  'api/v1/service/logs/components/levels/counts': {
    vNodeList: [
      {
        name: 'ambari',
        type: 0,
        logLevelCount: [
          {
            name: 'ERROR',
            value: '10'
          },
          {
            name: 'WARN',
            value: '50'
          }
        ],
        childs: [
          {
            name: 'hdfs',
            type: 2,
            logLevelCount: [
              {
                name: 'ERROR',
                value: '10'
              },
              {
                name: 'WARN',
                value: '20'
              }
            ],
            isParent: false,
            isRoot: false
          },
          {
            name: 'zookeeper',
            type: 3,
            logLevelCount: [
              {
                name: 'ERROR',
                value: '20'
              },
              {
                name: 'WARN',
                value: '40'
              }
            ],
            isParent: false,
            isRoot: false
          }
        ],
        isParent: true,
        isRoot: false
      },
      {
        name: 'ambari_agent',
        type: 1,
        logLevelCount: [
          {
            name: 'ERROR',
            value: '100'
          },
          {
            name: 'WARN',
            value: '500'
          }
        ],
        isParent: false,
        isRoot: false
      }
    ]
  },
  'api/v1/service/logs/files': {
    hostLogFiles: {
      clusters: clusters,
      services: services
    }
  },
  'api/v1/service/logs/histogram': {
    graphData: [
      {
        dataCount: [
          {
            name: currentTime.toISOString(),
            value: '1000'
          },
          {
            name: currentTime.clone().subtract(1, 'h').toISOString(),
            value: '2000'
          }
        ],
        name: 'ERROR'
      },
      {
        dataCount: [
          {
            name: currentTime.toISOString(),
            value: '700'
          },
          {
            name: currentTime.clone().subtract(1, 'h').toISOString(),
            value: '900'
          }
        ],
        name: 'WARN'
      }
    ]
  },
  'api/v1/service/logs/hosts': {
    groupList: hosts.map(host => Object.assign({}, {host}))
  },
  'api/v1/service/logs/schema/fields': [{
    'name': 'cluster',
    'label': 'Cluster',
    'filterable': true,
    'visible': false
  }, {
    'name': 'key_log_message',
    'label': 'Key Log Message',
    'filterable': true,
    'visible': false
  }, {
    'name': 'type',
    'label': 'Component',
    'filterable': true,
    'visible': true
  }, {
    'name': 'path',
    'label': 'Path',
    'filterable': true,
    'visible': false
  }, {
    'name': 'logtype',
    'label': 'Logtype',
    'filterable': true,
    'visible': false
  }, {
    'name': 'file',
    'label': 'File',
    'filterable': true,
    'visible': false
  }, {
    'name': 'line_number',
    'label': 'Line Number',
    'filterable': true,
    'visible': true
  }, {
    'name': 'host',
    'label': 'Host',
    'filterable': true,
    'visible': false
  }, {
    'name': 'log_message',
    'label': 'Message',
    'filterable': true,
    'visible': true
  }, {
    'name': 'logger_name',
    'label': 'Logger Name',
    'filterable': true,
    'visible': false
  }, {
    'name': 'logfile_line_number',
    'label': 'Logfile Line Number',
    'filterable': true,
    'visible': false
  }, {
    'name': 'group',
    'label': 'Group',
    'filterable': true,
    'visible': false
  }, {
    'name': 'method',
    'label': 'Method',
    'filterable': true,
    'visible': false
  }, {
    'name': 'level',
    'label': 'Level',
    'filterable': true,
    'visible': true
  }, {
    'name': 'ip',
    'label': 'Ip',
    'filterable': true,
    'visible': false
  }, {
    'name': 'thread_name',
    'label': 'Thread',
    'filterable': true,
    'visible': false
  }, {
    'name': 'logtime',
    'label': 'Log Time',
    'filterable': true,
    'visible': true
  }],
  'api/v1/service/logs/serviceconfig': '',
  'api/v1/service/logs/tree': {
    vNodeList: [
      {
        name: hosts[0],
        type: 'H',
        value: '1',
        childs: [
          {
            name: 'ams_collector',
            type: 'C',
            value: '1',
            logLevelCount: [
              {
                name: 'WARN',
                value: '1'
              }
            ],
            isParent: false,
            isRoot: false
          }
        ],
        logLevelCount: [
          {
            name: 'WARN',
            value: '1'
          }
        ],
        isParent: true,
        isRoot: true
      },
      {
        name: hosts[1],
        type: 'H',
        value: '6',
        childs: [
          {
            name: 'ams_collector',
            type: 'C',
            value: '1',
            logLevelCount: [
              {
                name: 'ERROR',
                value: '1'
              }
            ],
            isParent: false,
            isRoot: false
          },
          {
            name: 'ambari_agent',
            type: 'C',
            value: '1',
            logLevelCount: [
              {
                name: 'FATAL',
                value: '1'
              }
            ],
            isParent: false,
            isRoot: false
          },
          {
            name: 'zookeeper_server',
            type: 'C',
            value: '2',
            logLevelCount: [
              {
                name: 'INFO',
                value: '1'
              },
              {
                name: 'DEBUG',
                value: '1'
              }
            ],
            isParent: false,
            isRoot: false
          },
          {
            name: 'zookeeper_client',
            type: 'C',
            value: '2',
            logLevelCount: [
              {
                name: 'TRACE',
                value: '1'
              },
              {
                name: 'UNKNOWN',
                value: '1'
              }
            ],
            isParent: false,
            isRoot: false
          }
        ],
        logLevelCount: [
          {
            name: 'ERROR',
            value: '1'
          },
          {
            name: 'FATAL',
            value: '1'
          },
          {
            name: 'INFO',
            value: '1'
          },
          {
            name: 'DEBUG',
            value: '1'
          },
          {
            name: 'TRACE',
            value: '1'
          },
          {
            name: 'UNKNOWN',
            value: '1'
          }
        ],
        isParent: true,
        isRoot: true
      }
    ]
  },
  'api/v1/service/logs/truncated': {
    logList: [
      {
        path: '/var/log/ambari-metrics-collector/ambari-metrics-collector.log',
        host: 'h0',
        level: 'WARN',
        logtime: '2017-05-28T11:30:22.531Z',
        ip: '192.168.0.1',
        logfile_line_number: 8,
        type: 'ams_collector',
        _version_: 9,
        id: 'id2',
        file: 'ambari-metrics-collector.log',
        seq_num: 10,
        bundle_id: 'b2',
        case_id: 'c2',
        log_message: 'Connection refused',
        message_md5: '1357908642',
        cluster: 'cl2',
        event_count: 5,
        event_md5: '1908755391',
        event_dur_ms: 200,
        _ttl_: '+5DAYS',
        _expire_at_: '2017-05-29T11:30:22.531Z',
        _router_field_: 20
      },
      {
        path: '/var/log/ambari-metrics-collector/ambari-metrics-collector.log',
        host: 'h1',
        level: 'ERROR',
        logtime: '2017-05-28T10:30:22.531Z',
        ip: '192.168.0.2',
        type: 'ams_collector',
        _version_: 14,
        id: 'id3',
        file: 'ambari-metrics-collector.log',
        seq_num: 15,
        bundle_id: 'b3',
        case_id: 'c3',
        log_message: 'Connection refused',
        logfile_line_number: 16,
        message_md5: '1357908642',
        cluster: 'cl3',
        event_count: 2,
        event_md5: '1029384756',
        event_dur_ms: 700,
        _ttl_: '+5DAYS',
        _expire_at_: '2017-05-29T10:30:22.531Z',
        _router_field_: 5
      }
    ]
  },
  'api/v1/service/logs/clusters': clusters,

  'api/v1/status': {
    auditlogs: {
      znodeReady: true,
      solrCollectionReady: true,
      solrAliasReady: false,
      configurationUploaded: true
    },
    servicelogs: {
      znodeReady: true,
      solrCollectionReady: true,
      configurationUploaded: true
    },
    userconfig: {
      znodeReady: true,
      solrCollectionReady: true,
      configurationUploaded: true
    }
  },
  'api/v1/shipper/filters/\[a-zA-Z0-9\]/level': {
    'filter': {
      'ambari_agent': {
        'label': 'ambari_agent',
        'hosts': [],
        'defaultLevels': [
          'FATAL',
          'ERROR',
          'WARN'
        ],
        'overrideLevels': [],
        'expiryTime': null
      },
      'ambari_alerts': {
        'label': 'ambari_alerts',
        'hosts': [],
        'defaultLevels': [
          'FATAL',
          'ERROR',
          'WARN'
        ],
        'overrideLevels': [],
        'expiryTime': null
      },
      'ambari_server': {
        'label': 'ambari_server',
        'hosts': [],
        'defaultLevels': [
          'FATAL',
          'ERROR',
          'WARN'
        ],
        'overrideLevels': [],
        'expiryTime': null
      },
      'ams_collector': {
        'label': 'ams_collector',
        'hosts': [],
        'defaultLevels': [
          'FATAL',
          'ERROR',
          'WARN'
        ],
        'overrideLevels': [],
        'expiryTime': null
      },
      'ams_hbase_master': {
        'label': 'ams_hbase_master',
        'hosts': [],
        'defaultLevels': [
          'FATAL',
          'ERROR',
          'WARN'
        ],
        'overrideLevels': [],
        'expiryTime': null
      },
      'ams_monitor': {
        'label': 'ams_monitor',
        'hosts': [],
        'defaultLevels': [
          'FATAL',
          'ERROR',
          'WARN'
        ],
        'overrideLevels': [],
        'expiryTime': null
      },
      'logsearch_app': {
        'label': 'logsearch_app',
        'hosts': [],
        'defaultLevels': [
          'FATAL',
          'ERROR',
          'WARN'
        ],
        'overrideLevels': [],
        'expiryTime': null
      },
      'logsearch_feeder': {
        'label': 'logsearch_feeder',
        'hosts': [],
        'defaultLevels': [
          'FATAL',
          'ERROR',
          'WARN'
        ],
        'overrideLevels': [],
        'expiryTime': null
      }
    }
  },
  'api/v1/shipper/input/[a-zA-Z0-9]{1,}/services$': [
    'zookeeper',
    'ambari-infra',
    'logsearch',
    'ambari',
    'ambari-metrics',
    'hdfs'
  ],
  'api/v1/shipper/input/[a-zA-Z0-9\\-]{1,}/services/[a-zA-Z0-9\\-]{1,}$': {
    'input': [
      {
        'type': 'hdfs_datanode',
        'rowtype': 'service',
        'path': '/var/log/hadoop/hdfs/hadoop-hdfs-datanode-*.log'
      },
      {
        'type': 'hdfs_namenode',
        'rowtype': 'service',
        'path': '/var/log/hadoop/hdfs/hadoop-hdfs-namenode-*.log'
      },
      {
        'type': 'hdfs_journalnode',
        'rowtype': 'service',
        'path': '/var/log/hadoop/hdfs/hadoop-hdfs-journalnode-*.log'
      },
      {
        'type': 'hdfs_secondarynamenode',
        'rowtype': 'service',
        'path': '/var/log/hadoop/hdfs/hadoop-hdfs-secondarynamenode-*.log'
      },
      {
        'type': 'hdfs_zkfc',
        'rowtype': 'service',
        'path': '/var/log/hadoop/hdfs/hadoop-hdfs-zkfc-*.log'
      },
      {
        'type': 'hdfs_nfs3',
        'rowtype': 'service',
        'path': '/var/log/hadoop/hdfs/hadoop-hdfs-nfs3-*.log'
      },
      {
        'type': 'hdfs_audit',
        'rowtype': 'audit',
        'path': '/var/log/hadoop/hdfs/hdfs-audit.log',
        'add_fields': {
          'logType': 'HDFSAudit',
          'enforcer': 'hadoop-acl',
          'repoType': '1',
          'repo': 'hdfs'
        },
        'is_enabled': true
      }
    ],
    'filter': [
      {
        'filter': 'grok',
        'conditions': {
          'fields': {
            'type': [
              'hdfs_datanode',
              'hdfs_journalnode',
              'hdfs_secondarynamenode',
              'hdfs_namenode',
              'hdfs_zkfc',
              'hdfs_nfs3'
            ]
          }
        },
        'post_map_values': {
          'logtime': [
            {
              'map_date': {
                'target_date_pattern': 'yyyy-MM-dd HH:mm:ss,SSS'
              }
            }
          ]
        },
        'log4j_format': '%d{ISO8601} %-5p %c{2} (%F:%M(%L)) - %m%n',
        'multiline_pattern': '^(%{TIMESTAMP_ISO8601:logtime})',
        'message_pattern': '(?m)^%{TIMESTAMP_ISO8601:logtime}%{SPACE}%{LOGLEVEL:level}%{SPACE}%{JAVACLASS:logger_name}%{SPACE}\\(%{JAVAFILE:file}:%{JAVAMETHOD:method}\\(%{INT:line_number}\\)\\)%{SPACE}-%{SPACE}%{GREEDYDATA:log_message}'
      },
      {
        'filter': 'grok',
        'conditions': {
          'fields': {
            'type': [
              'hdfs_audit'
            ]
          }
        },
        'post_map_values': {
          'evtTime': [
            {
              'map_date': {
                'target_date_pattern': 'yyyy-MM-dd HH:mm:ss,SSS'
              }
            }
          ]
        },
        'log4j_format': '%d{ISO8601} %-5p %c{2} (%F:%M(%L)) - %m%n',
        'multiline_pattern': '^(%{TIMESTAMP_ISO8601:evtTime})',
        'message_pattern': '(?m)^%{TIMESTAMP_ISO8601:evtTime}%{SPACE}%{LOGLEVEL:level}%{SPACE}%{JAVACLASS:logger_name}:%{SPACE}%{GREEDYDATA:log_message}'
      },
      {
        'filter': 'keyvalue',
        'conditions': {
          'fields': {
            'type': [
              'hdfs_audit'
            ]
          }
        },
        'sort_order': 1,
        'source_field': 'log_message',
        'post_map_values': {
          'callerContext': [
            {
              'map_fieldname': {
                'new_field_name': 'req_caller_id'
              }
            }
          ],
          'src': [
            {
              'map_fieldname': {
                'new_field_name': 'resource'
              }
            }
          ],
          'allowed': [
            {
              'map_fieldvalue': {
                'pre_value': 'true',
                'post_value': '1'
              }
            },
            {
              'map_fieldvalue': {
                'pre_value': 'false',
                'post_value': '0'
              }
            },
            {
              'map_fieldname': {
                'new_field_name': 'result'
              }
            }
          ],
          'ip': [
            {
              'map_fieldname': {
                'new_field_name': 'cliIP'
              }
            }
          ],
          'proto': [
            {
              'map_fieldname': {
                'new_field_name': 'cliType'
              }
            }
          ],
          'cmd': [
            {
              'map_fieldname': {
                'new_field_name': 'action'
              }
            }
          ]
        },
        'field_split': '\t',
        'value_split': '='
      },
      {
        'filter': 'grok',
        'conditions': {
          'fields': {
            'type': [
              'hdfs_audit'
            ]
          }
        },
        'sort_order': 2,
        'source_field': 'ugi',
        'remove_source_field': false,
        'post_map_values': {
          'k_authType': [
            {
              'map_fieldname': {
                'new_field_name': 'proxyAuthType'
              }
            }
          ],
          'p_authType': [
            {
              'map_fieldname': {
                'new_field_name': 'authType'
              }
            }
          ],
          'x_user': [
            {
              'map_fieldname': {
                'new_field_name': 'reqUser'
              }
            }
          ],
          'k_user': [
            {
              'map_fieldname': {
                'new_field_name': 'proxyUsers'
              }
            }
          ],
          'p_user': [
            {
              'map_fieldname': {
                'new_field_name': 'reqUser'
              }
            }
          ],
          'user': [
            {
              'map_fieldname': {
                'new_field_name': 'reqUser'
              }
            }
          ]
        },
        'message_pattern': '%{USERNAME:p_user}.+auth:%{USERNAME:p_authType}.+via %{USERNAME:k_user}.+auth:%{USERNAME:k_authType}|%{USERNAME:user}.+auth:%{USERNAME:authType}|%{USERNAME:x_user}'
      }
    ]
  }
};
