<template>
  <div>
    <table class="table table-bordered">
      <thead>
      <tr>
        <th>序号</th>
        <th>api</th>
        <th>限额key</th>
        <th>额度</th>
        <th>时间（秒）</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="quotaLimit in quotaLimitList" v-bind:key="quotaLimit">
        <td>{{quotaLimit.id}}</td>
        <td>{{quotaLimit.api}}</td>
        <td>{{quotaLimit.quotaKey}}</td>
        <td>{{quotaLimit.quota}}</td>
        <td>{{quotaLimit.timeSpan}}</td>
        <td><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modifyQuotaLimit">修改</button><button type="button" class="btn btn-danger">删除</button></td>
      </tr>
      </tbody>
    </table>
    <div><button type="button" style="float:right" class="btn btn-success" data-toggle="modal" data-target="#addQuotaLimit">新增</button></div>
    <div class="modal fade" id="modifyQuotaLimit" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
              &times;
            </button>
            <h4 class="modal-title" id="myModalLabel">
              限额配置
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
                <span class="input-group-addon">限额key</span>
                <input id="modifyKey" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">额度</span>
                <input id="modifyQuota" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">时间（秒）</span>
                <input id="modifyTimeSpan" type="text" class="form-control" placeholder="">
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
        </div>
      </div>
    </div>
    <div class="modal fade" id="addQuotaLimit" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
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
                <input id="addId" type="text" class="form-control" placeholder="">
              </div>
              <div class="input-group">
                <span class="input-group-addon">api</span>
                <input id="addApi" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">限额key</span>
                <input id="addKey" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">限额值</span>
                <input id="addQuota" type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">时间（秒）</span>
                <input id="addTimeSpan" type="text" class="form-control" placeholder="">
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
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuotaLimit',
  data () {
    return {
      quotaLimitList: [
        {
          id: '',
          api: '',
          quotaKey: '',
          quota: '',
          timeSpan: ''
        }
      ]
    }
  },
  mounted: function () {
    this.listQuotaLimitConfig()
  },
  methods: {
    listQuotaLimitConfig: function () {
      const axios = require('axios').default
      axios
        .get('http://localhost:8100/gateway/list-quota-limit-config')
        .then(response => {
          console.log(response)
          this.quotaLimitList = response.data
        })
        .cached(function (error) {
          console.log(error)
        })
    }
  }
}
$(function () {
  $('#modifyQuotaLimit').on('show.bs.modal', function (event) {
    var rateLimitButton = $(event.relatedTarget); //触发事件的按钮
    var id = rateLimitButton.closest('tr').find('td').eq(0).text();
    var api = rateLimitButton.closest('tr').find('td').eq(1).text();
    var quotaKey = rateLimitButton.closest('tr').find('td').eq(2).text();
    var quota = rateLimitButton.closest('tr').find('td').eq(3).text();
    var timeSpan = rateLimitButton.closest('tr').find('td').eq(4).text();
    $("#modifyId").attr("value", id)
    $("#modifyApi").attr("value", api)
    $("#modifyKey").attr("value", quotaKey)
    $("#modifyQuota").attr("value", quota)
    $("#modifyTimeSpan").attr("value", timeSpan)
  }),
  $('#addQuotaLimit').on('show.bs.modal', function (event) {
    $("#addId").val("")
    $("#addApi").val("")
    $("#addKey").val("")
    $("#addQuota").val("")
    $("#addTimeSpan").val("")
  })
})
</script>

<style scoped>
</style>
