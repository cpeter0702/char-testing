import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStockRecords, NewStockRecords } from '../stock-records.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStockRecords for edit and NewStockRecordsFormGroupInput for create.
 */
type StockRecordsFormGroupInput = IStockRecords | PartialWithRequiredKeyOf<NewStockRecords>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStockRecords | NewStockRecords> = Omit<T, 'businessDate'> & {
  businessDate?: string | null;
};

type StockRecordsFormRawValue = FormValueOf<IStockRecords>;

type NewStockRecordsFormRawValue = FormValueOf<NewStockRecords>;

type StockRecordsFormDefaults = Pick<NewStockRecords, 'id' | 'businessDate'>;

type StockRecordsFormGroupContent = {
  id: FormControl<StockRecordsFormRawValue['id'] | NewStockRecords['id']>;
  sticker: FormControl<StockRecordsFormRawValue['sticker']>;
  businessDate: FormControl<StockRecordsFormRawValue['businessDate']>;
  stockPrice: FormControl<StockRecordsFormRawValue['stockPrice']>;
};

export type StockRecordsFormGroup = FormGroup<StockRecordsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockRecordsFormService {
  createStockRecordsFormGroup(stockRecords: StockRecordsFormGroupInput = { id: null }): StockRecordsFormGroup {
    const stockRecordsRawValue = this.convertStockRecordsToStockRecordsRawValue({
      ...this.getFormDefaults(),
      ...stockRecords,
    });
    return new FormGroup<StockRecordsFormGroupContent>({
      id: new FormControl(
        { value: stockRecordsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      sticker: new FormControl(stockRecordsRawValue.sticker),
      businessDate: new FormControl(stockRecordsRawValue.businessDate),
      stockPrice: new FormControl(stockRecordsRawValue.stockPrice, {
        validators: [Validators.required],
      }),
    });
  }

  getStockRecords(form: StockRecordsFormGroup): IStockRecords | NewStockRecords {
    return this.convertStockRecordsRawValueToStockRecords(form.getRawValue() as StockRecordsFormRawValue | NewStockRecordsFormRawValue);
  }

  resetForm(form: StockRecordsFormGroup, stockRecords: StockRecordsFormGroupInput): void {
    const stockRecordsRawValue = this.convertStockRecordsToStockRecordsRawValue({ ...this.getFormDefaults(), ...stockRecords });
    form.reset(
      {
        ...stockRecordsRawValue,
        id: { value: stockRecordsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StockRecordsFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      businessDate: currentTime,
    };
  }

  private convertStockRecordsRawValueToStockRecords(
    rawStockRecords: StockRecordsFormRawValue | NewStockRecordsFormRawValue,
  ): IStockRecords | NewStockRecords {
    return {
      ...rawStockRecords,
      businessDate: dayjs(rawStockRecords.businessDate, DATE_TIME_FORMAT),
    };
  }

  private convertStockRecordsToStockRecordsRawValue(
    stockRecords: IStockRecords | (Partial<NewStockRecords> & StockRecordsFormDefaults),
  ): StockRecordsFormRawValue | PartialWithRequiredKeyOf<NewStockRecordsFormRawValue> {
    return {
      ...stockRecords,
      businessDate: stockRecords.businessDate ? stockRecords.businessDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
