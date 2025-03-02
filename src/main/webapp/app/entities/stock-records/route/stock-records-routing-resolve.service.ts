import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStockRecords } from '../stock-records.model';
import { StockRecordsService } from '../service/stock-records.service';

const stockRecordsResolve = (route: ActivatedRouteSnapshot): Observable<null | IStockRecords> => {
  const id = route.params['id'];
  if (id) {
    return inject(StockRecordsService)
      .find(id)
      .pipe(
        mergeMap((stockRecords: HttpResponse<IStockRecords>) => {
          if (stockRecords.body) {
            return of(stockRecords.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default stockRecordsResolve;
