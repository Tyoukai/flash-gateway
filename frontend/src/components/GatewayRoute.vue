<template>
  <div>
    <table class="table table-bordered">
      <thead>
      <tr>
        <th>api标识</th>
        <th>uri</th>
        <th>predicates</th>
        <th>filters</th>
        <th>order</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>neo_route</td>
        <td>http://www.ityouknow.com</td>
        <td>[{"name":"Path","args":{"_genkey_0":"/spring-cloud"}}]</td>
        <td>[]</td>
        <td>0</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
  export default {
    name: 'GatewayRoute',
  }
</script>

<style scoped>
</style>
