<template>
  <div id="rateLimitDiv">
    <table class="table table-bordered">
      <thead>
      <tr>
        <th>序号</th>
        <th>api</th>
        <th>限流key</th>
        <th>QPS</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="rateLimit in rateLimitList" v-bind:key="rateLimit">
        <td>{{rateLimit.id}}</td>
        <td>{{rateLimit.api}}</td>
        <td>{{rateLimit.rateKey}}</td>
        <td>{{rateLimit.qps}}</td>
        <td><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modifyRateLimit">修改</button><button type="button" v-on:click="deleteRateLimitConfig" class="btn btn-danger">删除</button></td>
      </tr>
      </tbody>
    </table>
    <div><button type="button" style="float:right" class="btn btn-success" data-toggle="modal" data-target="#modifyRateLimit">新增</button></div>
    <div class="modal fade" id="modifyRateLimit" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
              &times;
            </button>
            <h4 class="modal-title" id="myModalLabel">
              限流配置
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
                <span class="input-group-addon">限流key</span>
                <input id="modifyKey" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">QPS</span>
                <input id="modifyQps" type="text" class="form-control" placeholder="">
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">关闭
            </button>
            <button type="button" class="btn btn-success" data-dismiss="modal" v-on:click="addOrupdateRateLimitConfig">
              确认
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RateLimit',
  data () {
    return {
      rateLimitList: [
        {id: '',
          api: '',
          rateKey: '',
          qps: ''
        }
      ]
    }
  },
  mounted: function () {
    this.listRateLimitConfig()
  },
  methods: {
    listRateLimitConfig: function () {
      const axios = require('axios').default
      axios
        .get('http://localhost:8100/gateway/list-rate-limit-config')
        .then(response => {
          console.log(response)
          this.rateLimitList = response.data
        })
        .cached(function (error) {
          console.log(error)
        })
    },
    addOrupdateRateLimitConfig: function () {
      var id = $("#modifyId").val()
      var api = $("#modifyApi").val()
      var rateKey = $("#modifyKey").val()
      var qps = $("#modifyQps").val()
      if (typeof rateKey == "undefined" || rateKey == null || rateKey == "") {
        alert("限流key不能为空")
        return
      }
      if (typeof qps == "undefined" || qps == null || qps <= 0) {
        alert("限流qps不能为空或小于等于0")
        return
      }

      const axios = require('axios').default
      axios
        .get('http://localhost:8100/gateway/add-update-rate-limit-config', {
          params: {
            id: id,
            api: api,
            rateKey: rateKey,
            qps: qps
          }
        })
        .then(response => {
          console.log(response.data)
          this.listRateLimitConfig()
        })
        .cached(function (error) {
          console.log(error)
        })
    },
    deleteRateLimitConfig: function () {
      alert("delete")
    }
  }
}
$(function () {
  $('#modifyRateLimit').on('show.bs.modal', function (event) {
    var rateLimitButton = $(event.relatedTarget); //触发事件的按钮
    var id = rateLimitButton.closest('tr').find('td').eq(0).text();
    var api = rateLimitButton.closest('tr').find('td').eq(1).text();
    var rateKey = rateLimitButton.closest('tr').find('td').eq(2).text();
    var qps = rateLimitButton.closest('tr').find('td').eq(3).text();
    $("#modifyId").attr("value", id)
    $("#modifyApi").attr("value", api)
    $("#modifyKey").attr("value", rateKey)
    $("#modifyQps").attr("value", qps)
  })
})
</script>

<style scoped>
</style>
