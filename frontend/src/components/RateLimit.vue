<template>
  <div>
    <table class="table table-bordered">
      <thead>
      <tr>
        <th>序号</th>
        <th>api</th>
        <th>限流key</th>
        <th>aps</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>1</td>
        <td>rate.limit.test.api</td>
        <td>apiId:rate.limit.test.api</td>
        <td>1</td>
        <td><button type="button" class="btn btn-primary">修改</button></td>
      </tr>
      </tbody>
    </table>
    <div class="col-md-11 col-md-offset-6"><button type="button" class="btn btn-success">新增</button></div>

  </div>
</template>

<script>
export default {
  name: 'RateLimit'
}
</script>

<style scoped>
</style>
