import { MetricsPanelCtrl } from 'grafana/app/plugins/sdk';
import defaultsDeep from 'lodash/defaultsDeep';

interface KeyValue {
  key: string;
  value: any;
}

export default class SimpleCtrl extends MetricsPanelCtrl {
  static templateUrl = 'partials/module.html';
  ace = require('src-noconflict/ace.js');
  aceTheme = require('ace-builds/src-noconflict/theme-monokai.js');
  sql = null;

  panelDefaults = {
    text: 'Hello test',
  };

  // Simple example showing the last value of all data
  firstValues: KeyValue[] = [];

  /** @ngInject */
  constructor($scope, $injector) {
    super($scope, $injector);
    defaultsDeep(this.panel, this.panelDefaults);

    this.events.on('data-received', this.initSqualEditor.bind(this));
  }

  sendSQuaLquery() {
    // sending to the client
    const connection = new WebSocket('ws://localhost:7777');

    connection.onopen = () => {
      console.log('Connected!');
      // @ts-ignore
      connection.send(this.sql.getValue()); // Send the message 'Ping' to the server
    };

    // Log errors
    connection.onerror = error => {
      console.log('WebSocket Error ' + error);
    };

    // Log messages from the server
    connection.onmessage = e => {
      console.log('Server: ' + e.data);
    };

    return false;
  }

  initSqualEditor() {
    this.sql = this.ace.edit('squal-editor');
    // @ts-ignore
    this.sql.setTheme(this.aceTheme);
    // @ts-ignore
    this.sql.session.setMode('ace/mode/sql');
  }
}

export { SimpleCtrl as PanelCtrl };
