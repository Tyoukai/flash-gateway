import Vue from 'vue'
import Router from 'vue-router'
import GatewayRoute from '@/components/GatewayRoute'
import HOME from '@/components/HOME'
import ParameterMapping from '@/components/ParameterMapping'
import QuotaLimit from '@/components/QuotaLimit'
import RateLimit from '@/components/RateLimit'

Vue.use(Router)

export default new Router({
  routes: [
    // {
    //     //   path: '/',
    //     //   name: 'HelloWorld',
    //     //   component: HelloWorld
    //     // },
    {
      path: '/',
      name: 'HOME',
      component: HOME
    },
    {
      path: '/gatewayRoute',
      name: 'GatewayRoute',
      component: GatewayRoute
    },
    {
      path: '/parameterMapping',
      name: 'ParameterMapping',
      component: ParameterMapping
    },
    {
      path: '/quotaLimit',
      name: 'QuotaLimit',
      component: QuotaLimit
    },
    {
      path: '/rateLimit',
      name: 'RateLimit',
      component: RateLimit
    }
  ]
})
