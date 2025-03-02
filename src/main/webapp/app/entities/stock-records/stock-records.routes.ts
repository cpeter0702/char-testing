import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { StockRecordsComponent } from './list/stock-records.component';
import { StockRecordsDetailComponent } from './detail/stock-records-detail.component';
import { StockRecordsUpdateComponent } from './update/stock-records-update.component';
import StockRecordsResolve from './route/stock-records-routing-resolve.service';

const stockRecordsRoute: Routes = [
  {
    path: '',
    component: StockRecordsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StockRecordsDetailComponent,
    resolve: {
      stockRecords: StockRecordsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StockRecordsUpdateComponent,
    resolve: {
      stockRecords: StockRecordsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StockRecordsUpdateComponent,
    resolve: {
      stockRecords: StockRecordsResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default stockRecordsRoute;
