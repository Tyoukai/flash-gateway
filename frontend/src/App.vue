<template>
  <div id="app">
    <!--<img src="./assets/logo.png">-->
    <nav class="navbar navbar-default" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
          <a class="navbar-brand" href="http://localhost:8080/#/">首页</a>
        </div>
        <div>
          <ul class="nav navbar-nav">
            <li class="active"><a href="http://localhost:8080/#/gatewayRoute">路由配置</a></li>
            <li><a href="http://localhost:8080/#/rateLimit">限流配置</a></li>
            <li><a href="http://localhost:8080/#/quotaLimit">限额配置</a></li>
            <li><a href="http://localhost:8080/#/parameterMapping">参数映射</a></li>
          </ul>
        </div>
      </div>
    </nav>
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0px;
}
</style>
