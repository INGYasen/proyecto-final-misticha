export const environment = {
  production: false,
  apiUrl: 'http://localhost:7091',
  mercadoPagoPublicKey: 'APP_USR-7c1fc4cb-308b-4122-b356-65cc2ff76139',
  keycloak: {
    url: 'http://localhost:9091',
    realm: 'raymisupreme-dev',
    clientId: 'raymisupreme-spa',
    redirectUri: 'http://localhost:4200/login',
  },
};
