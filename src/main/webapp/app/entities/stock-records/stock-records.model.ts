import dayjs from 'dayjs/esm';

export interface IStockRecords {
  id: number;
  sticker?: string | null;
  businessDate?: dayjs.Dayjs | null;
  stockPrice?: number | null;
}

export type NewStockRecords = Omit<IStockRecords, 'id'> & { id: null };
