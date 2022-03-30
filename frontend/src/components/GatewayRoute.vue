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
        <td>{{gatewayRoute.api}}</td>
        <td>{{gatewayRoute.uri}}</td>
        <td>{{gatewayRoute.predicates}}</td>
        <td>{{gatewayRoute.filters}}</td>
        <td>{{gatewayRoute.order}}</td>
        <td><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modifyGatewayRoute">修改</button><button type="button" v-on:click="deleteRateLimitConfig(rateLimit.id, rateLimit.api)" class="btn btn-danger">删除</button></td>
      </tr>
      </tbody>
    </table>
    <div><button type="button" style="float:right" class="btn btn-success" data-toggle="modal" data-target="#modifyGatewayRoute">新增</button></div>
    <div class="modal fade" id="modifyGatewayRoute" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
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
          api: '',
          uri: '',
          predicates: '',
          filters: '',
          order: ''
        }
      ]
    }
  },
  mounted: function () {
    this.listgatewayRouteConfig()
  },
  methods: {
    listgatewayRouteConfig: function () {
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
    }
  }
}
</script>

<style scoped>
</style>
