import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStockRecords } from '../stock-records.model';
import { StockRecordsService } from '../service/stock-records.service';
import { StockRecordsFormService, StockRecordsFormGroup } from './stock-records-form.service';

@Component({
  standalone: true,
  selector: 'jhi-stock-records-update',
  templateUrl: './stock-records-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class StockRecordsUpdateComponent implements OnInit {
  isSaving = false;
  stockRecords: IStockRecords | null = null;

  protected stockRecordsService = inject(StockRecordsService);
  protected stockRecordsFormService = inject(StockRecordsFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StockRecordsFormGroup = this.stockRecordsFormService.createStockRecordsFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockRecords }) => {
      this.stockRecords = stockRecords;
      if (stockRecords) {
        this.updateForm(stockRecords);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const stockRecords = this.stockRecordsFormService.getStockRecords(this.editForm);
    if (stockRecords.id !== null) {
      this.subscribeToSaveResponse(this.stockRecordsService.update(stockRecords));
    } else {
      this.subscribeToSaveResponse(this.stockRecordsService.create(stockRecords));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStockRecords>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(stockRecords: IStockRecords): void {
    this.stockRecords = stockRecords;
    this.stockRecordsFormService.resetForm(this.editForm, stockRecords);
  }
}
