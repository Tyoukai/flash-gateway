<template>
  <div>
    <table class="table table-bordered">
      <thead>
      <tr>
        <th>序号</th>
        <th>api标识</th>
        <th>uri</th>
        <th>predicates</th>
        <th>filters</th>
        <th>order</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="gatewayRoute in gatewayRouteList" v-bind:key="gatewayRoute">
        <td>{{gatewayRoute.id}}</td>
        <td>{{gatewayRoute.apiId}}</td>
        <td>{{gatewayRoute.uri}}</td>
        <td>{{gatewayRoute.predicates}}</td>
        <td>{{gatewayRoute.filters}}</td>
        <td>{{gatewayRoute.order}}</td>
        <td><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modifyGatewayRoute">修改</button><button type="button" v-on:click="deleteGatewayRouteConfig(gatewayRoute.id, gatewayRoute.apiId)" class="btn btn-danger">删除</button></td>
      </tr>
      </tbody>
    </table>
    <div><button type="button" style="float:right" class="btn btn-success" data-toggle="modal" data-target="#addGatewayRoute">新增</button></div>
    <div class="modal fade" id="modifyGatewayRoute" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
              &times;
            </button>
            <h4 class="modal-title" id="myModalLabel">
              修改路由配置
            </h4>
          </div>
          <div class="modal-body">
            <form class="bs-example bs-example-form" role="form">
              <div class="input-group" style="display:none;">
                <span class="input-group-addon">id</span>
                <input id="modifyId" type="text" class="form-control" placeholder="">
              </div>
              <div class="input-group">
                <span class="input-group-addon">api</span>
                <input id="modifyApi" disabled="disabled" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">uri</span>
                <input id="modifyUri" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">predicates</span>
                <input id="modifyPredicates" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">filters</span>
                <input id="modifyFilters" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">order</span>
                <input id="modifyOrder" type="text" class="form-control" placeholder="">
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">关闭
            </button>
            <button type="button" class="btn btn-success" data-dismiss="modal">
              确认
            </button>
          </div>
        </div><!-- /.modal-content -->
      </div><!-- /.modal -->
    </div>
    <div class="modal fade" id="addGatewayRoute" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
              &times;
            </button>
            <h4 class="modal-title" id="myModalLabel">
              路由配置
            </h4>
          </div>
          <div class="modal-body">
            <form class="bs-example bs-example-form" role="form">
              <div class="input-group">
                <span class="input-group-addon">api</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">uri</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">predicates</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">filters</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">order</span>
                <input type="text" class="form-control" placeholder="">
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">关闭
            </button>
            <button type="button" class="btn btn-primary">
              确认
            </button>
          </div>
        </div><!-- /.modal-content -->
      </div><!-- /.modal -->
    </div>
  </div>
</template>

<script>
export default {
  name: 'GatewayRoute',
  data () {
    return {
      gatewayRouteList: [
        {id: '',
          apiId: '',
          uri: '',
          predicates: '',
          filters: '',
          order: ''
        }
      ]
    }
  },
  mounted: function () {
    this.listGatewayRouteConfig()
  },
  methods: {
    listGatewayRouteConfig: function () {
      const axios = require('axios').default
      axios
        .get('http://localhost:8100/gateway/list-gateway-route-config')
        .then(response => {
          console.log(response)
          this.gatewayRouteList = response.data
        })
        .cached(function (error) {
          console.log(error)
        })
    },
    updategatewayRouteConfig: function () {
      var id = $("#modifyId").val()
      var api = $("#modifyApi").val()
      var uri = $("#modifyUri").val()
      var predicates = $("#modifyPredicates").val()
      var filters = $("#modifyFilters").val()
      var order = $("#modifyOrder").val()
      if (typeof uri == "undefined" || uri == null || uri == "") {
        alert("uri不能为空")
        return
      }

      const axios = require('axios').default
      axios
        .get('http://localhost:8100/gateway/add-update-gateway-route-config', {
          params: {
            id: id,
            api: api,
            uri: uri,
            predicates: predicates,
            filters: filters,
            order: order
          }
        })
        .then(response => {
          console.log(response.data)
          this.listGatewayRouteConfig()
        })
        .cached(function (error) {
          console.log(error)
        })
    },
    addGatewayRouteConfig: function () {
      var id = $("#addId").val()
      var api = $("#addApi").val()
      var uri = $("#addUri").val()
      var predicates = $("#addPredicates").val()
      var filters = $("#addFilters").val()
      var order = $("#addOrder").val()
      if (typeof api == "undefined" || api == null || api == "") {
        alert("api不能为空")
        return
      }
      if (typeof uri == "undefined" || uri == null || uri <= 0) {
        alert("uri不能为空")
        return
      }

      const axios = require('axios').default
      axios
        .get('http://localhost:8100/gateway/add-update-gateway-route-config', {
          params: {
            id: id,
            api: api,
            uri: uri,
            predicates: predicates,
            filters: filters,
            order: order
          }
        })
        .then(response => {
          console.log(response.data)
          this.listGatewayRouteConfig()
        })
        .cached(function (error) {
          console.log(error)
        })
    },
    deleteGatewayRouteConfig: function (id, api) {
      const axios = require('axios').default
      axios
        .get('http://localhost:8100/gateway/delete-gateway-route-config', {
          params: {
            id: id,
            api: api
          }
        })
        .then(response => {
          console.log(response.data)
          this.listGatewayRouteConfig()
        })
        .cached(function (error) {
          console.log(error)
        })
    }
  }
}
$(function () {
  $('#modifyGatewayRoute').on('show.bs.modal', function (event) {
    var gatewayRouteButton = $(event.relatedTarget); //触发事件的按钮
    var id = gatewayRouteButton.closest('tr').find('td').eq(0).text();
    var api = gatewayRouteButton.closest('tr').find('td').eq(1).text();
    var uri = gatewayRouteButton.closest('tr').find('td').eq(2).text();
    var predicates = gatewayRouteButton.closest('tr').find('td').eq(3).text();
    var filters = gatewayRouteButton.closest('tr').find('td').eq(4).text();
    var order = gatewayRouteButton.closest('tr').find('td').eq(5).text();
    $("#modifyId").attr("value", id)
    $("#modifyApi").attr("value", api)
    $("#modifyUri").attr("value", uri)
    $("#modifyPredicates").attr("value", predicates)
    $("#modifyFilters").attr("value", filters)
    $("#modifyOrder").attr("value", order)
  }),
  $('#addGatewayRoute').on('show.bs.modal', function (event) {
    $("#addId").val("")
    $("#addApi").val("")
    $("#addUri").val("")
    $("#addPredicates").val("")
    $("#addFilters").val("")
    $("#addOrder").val("")
  })
})
</script>

<style scoped>
</style>
