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
      <tr>
        <td>1</td>
        <td>neo_route</td>
        <td>http://www.ityouknow.com</td>
        <td>[{"name":"Path","args":{"_genkey_0":"/spring-cloud"}}]</td>
        <td>[]</td>
        <td>0</td>
        <td><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modifyGatewayRoute">修改</button></td>
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
  </div>
</template>

<script>
export default {
  name: 'GatewayRoute'
}
</script>

<style scoped>
</style>
