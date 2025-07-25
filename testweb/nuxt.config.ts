// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },

  modules: [
    '@nuxt/ui',
    '@nuxt/icon',
    '@nuxt/eslint',
    '@nuxt/image',
    '@nuxt/fonts'
  ],
  runtimeConfig: {
    // Default values (used in all environments)
    public: {
      frontendHost: 'http://localhost', // Default value
    },
    // Environment-specific overrides
    development: {
      public: {
        frontendHost: 'http://192.168.5.211',
      },
    },
    staging: {
      public: {
        frontendHost: 'https://www.mystag.com',
      },
    },
    production: {
      public: {
        frontendHost: 'https://www.myprod.com',
      },
    },
  },
 
})