// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  app: {
    head: {
      title: 'Artillery online',
    },
  },
  compatibilityDate: '2024-04-03',
  devtools: { enabled: true },
  ssr: false,
  css: [
      'vuetify/lib/styles/main.sass',
      '@/assets/css/main.css'
  ],
  build: {
    transpile: ['vuetify']
  },
  vite: {
    define: {
      'process.env.DEBUG': 'false',
      'global': {},
    },
    server: {
      allowedHosts: ['host.docker.internal'],
    }
  },
  modules: [
    '@pinia/nuxt',
  ],
})
