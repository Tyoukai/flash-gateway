<template>
  <div>
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
      <tr>
        <td>1</td>
        <td>rate.limit.test.api</td>
        <td>apiId:rate.limit.test.api</td>
        <td>1</td>
        <td><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modifyRateLimit">修改</button></td>
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
              <div class="input-group">
                <span class="input-group-addon">api</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">限流key</span>
                <input type="text" class="form-control" placeholder="">
              </div>
              <br>
              <div class="input-group">
                <span class="input-group-addon">QPS</span>
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
  name: 'RateLimit'
}
</script>

<style scoped>
</style>
