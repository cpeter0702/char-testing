import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStockRecords, NewStockRecords } from '../stock-records.model';

export type PartialUpdateStockRecords = Partial<IStockRecords> & Pick<IStockRecords, 'id'>;

type RestOf<T extends IStockRecords | NewStockRecords> = Omit<T, 'businessDate'> & {
  businessDate?: string | null;
};

export type RestStockRecords = RestOf<IStockRecords>;

export type NewRestStockRecords = RestOf<NewStockRecords>;

export type PartialUpdateRestStockRecords = RestOf<PartialUpdateStockRecords>;

export type EntityResponseType = HttpResponse<IStockRecords>;
export type EntityArrayResponseType = HttpResponse<IStockRecords[]>;

@Injectable({ providedIn: 'root' })
export class StockRecordsService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/stock-records');

  create(stockRecords: NewStockRecords): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockRecords);
    return this.http
      .post<RestStockRecords>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(stockRecords: IStockRecords): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockRecords);
    return this.http
      .put<RestStockRecords>(`${this.resourceUrl}/${this.getStockRecordsIdentifier(stockRecords)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(stockRecords: PartialUpdateStockRecords): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockRecords);
    return this.http
      .patch<RestStockRecords>(`${this.resourceUrl}/${this.getStockRecordsIdentifier(stockRecords)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestStockRecords>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStockRecords[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStockRecordsIdentifier(stockRecords: Pick<IStockRecords, 'id'>): number {
    return stockRecords.id;
  }

  compareStockRecords(o1: Pick<IStockRecords, 'id'> | null, o2: Pick<IStockRecords, 'id'> | null): boolean {
    return o1 && o2 ? this.getStockRecordsIdentifier(o1) === this.getStockRecordsIdentifier(o2) : o1 === o2;
  }

  addStockRecordsToCollectionIfMissing<Type extends Pick<IStockRecords, 'id'>>(
    stockRecordsCollection: Type[],
    ...stockRecordsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const stockRecords: Type[] = stockRecordsToCheck.filter(isPresent);
    if (stockRecords.length > 0) {
      const stockRecordsCollectionIdentifiers = stockRecordsCollection.map(stockRecordsItem =>
        this.getStockRecordsIdentifier(stockRecordsItem),
      );
      const stockRecordsToAdd = stockRecords.filter(stockRecordsItem => {
        const stockRecordsIdentifier = this.getStockRecordsIdentifier(stockRecordsItem);
        if (stockRecordsCollectionIdentifiers.includes(stockRecordsIdentifier)) {
          return false;
        }
        stockRecordsCollectionIdentifiers.push(stockRecordsIdentifier);
        return true;
      });
      return [...stockRecordsToAdd, ...stockRecordsCollection];
    }
    return stockRecordsCollection;
  }

  protected convertDateFromClient<T extends IStockRecords | NewStockRecords | PartialUpdateStockRecords>(stockRecords: T): RestOf<T> {
    return {
      ...stockRecords,
      businessDate: stockRecords.businessDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restStockRecords: RestStockRecords): IStockRecords {
    return {
      ...restStockRecords,
      businessDate: restStockRecords.businessDate ? dayjs(restStockRecords.businessDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestStockRecords>): HttpResponse<IStockRecords> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestStockRecords[]>): HttpResponse<IStockRecords[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
