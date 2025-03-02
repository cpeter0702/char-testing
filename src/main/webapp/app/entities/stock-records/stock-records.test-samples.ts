import dayjs from 'dayjs/esm';

import { IStockRecords, NewStockRecords } from './stock-records.model';

export const sampleWithRequiredData: IStockRecords = {
  id: 1424,
  stockPrice: 31439.78,
};

export const sampleWithPartialData: IStockRecords = {
  id: 24072,
  sticker: 'upon',
  stockPrice: 20425.1,
};

export const sampleWithFullData: IStockRecords = {
  id: 6554,
  sticker: 'lean orange',
  businessDate: dayjs('2025-03-01T10:21'),
  stockPrice: 29134.7,
};

export const sampleWithNewData: NewStockRecords = {
  stockPrice: 27811.48,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
