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
      <tr>
        <td>1</td>
        <td>quota.limit.test.api</td>
        <td>userId:6666</td>
        <td>50</td>
        <td>1800</td>
        <td><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modifyQuotaLimit">修改</button></td>
      </tr>
      </tbody>
    </table>
    <div><button type="button" style="float:right" class="btn btn-success" data-toggle="modal" data-target="#modifyQuotaLimit">新增</button></div>
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
              <div class="input-group">
                <span class="input-group-addon">api</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">限额key</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">额度</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">时间（秒）</span>
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
  name: 'QuotaLimit'
}
</script>

<style scoped>
</style>
