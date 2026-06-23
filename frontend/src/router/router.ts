import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import CatCansView from '../views/CatCansView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import AdminCatCanImportView from '../views/AdminCatCanImportView.vue'
import AdminCatCanListView from '../views/AdminCatCanListView.vue'
import AdminCatCanEditView from '../views/AdminCatCanEditView.vue'
import { useAuthStore } from '@/stores/auth'
import { resolvePostLoginRedirect } from '@/utils/loginRedirect'

const routes: Array<RouteRecordRaw> = [
  { path: '/', redirect: '/cans' },
  { path: '/home', component: HomeView },
  { path: '/cans', component: CatCansView },
  {
    path: '/admin/cans/import',
    component: AdminCatCanImportView,
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/admin/cans',
    component: AdminCatCanListView,
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/admin/cans/:id/edit',
    component: AdminCatCanEditView,
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  { path: '/login', component: LoginView, meta: { guest: true } },
  { path: '/register', component: RegisterView, meta: { guest: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { path: '/cans' }
  }

  if (to.meta.guest && auth.isLoggedIn) {
    return resolvePostLoginRedirect(to.query.redirect)
  }

  return true
})

export default router
