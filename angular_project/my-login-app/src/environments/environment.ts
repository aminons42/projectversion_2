export const environment = {
  gatewayUrl: 'http://localhost:8084',
  
  // Routes directes au service (contournement du Gateway pour le debug)
  authUrl: 'http://localhost:8080/api/auth',
  usersUrl: 'http://localhost:8084/api/users',
  incidentsUrl: 'http://localhost:8084/api/incidents',
  auditsUrl: 'http://localhost:8084/api/audits',
  nonConformitesUrl: 'http://localhost:8084/api/non-conformites',
  templatesUrl: 'http://localhost:8084/api/templates',
  plansUrl: 'http://localhost:8084/api/planaction/plans',
  actionsUrl: 'http://localhost:8084/api/actions',
  escaladesUrl: 'http://localhost:8084/api/escalades',
  verificationUrl: 'http://localhost:8084/api/verification',
  suivisUrl: 'http://localhost:8084/api/suivis'
};