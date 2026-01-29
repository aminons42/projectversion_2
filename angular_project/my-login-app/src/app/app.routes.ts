import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { IncidentsPage } from './incidents/incidents';
import { AuditsPage } from './audits/audits';
import { PlansActionPage } from './plans-action/plans-action';
import { UtilisateursComponent } from './pages/utilisateurs/utilisateurs.component';
import { ProfilePage } from './profile/profile';
import { NonConformitesComponent } from './pages/non-conformites/non-conformites.component';
import { TemplatesComponent } from './pages/templates/templates.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    {path:  '' , redirectTo:'login', pathMatch:'full'},
    {path: 'login', component: Login},
    {path: 'dashboard', component: Dashboard, canActivate: [authGuard]},
    {path: 'incidents', component: IncidentsPage, canActivate: [authGuard]},
    {path: 'audits', component: AuditsPage, canActivate: [authGuard]},
    {path: 'plans-actions', component: PlansActionPage, canActivate: [authGuard]},
    {path: 'profil', component: ProfilePage, canActivate: [authGuard]},
    {path: 'utilisateurs', component: UtilisateursComponent, canActivate: [authGuard]},
    {path: 'non-conformites', component: NonConformitesComponent, canActivate: [authGuard]},
    {path: 'templates', component: TemplatesComponent, canActivate: [authGuard]}
];
