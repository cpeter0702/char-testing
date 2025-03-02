import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IStockRecords } from '../stock-records.model';

@Component({
  standalone: true,
  selector: 'jhi-stock-records-detail',
  templateUrl: './stock-records-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class StockRecordsDetailComponent {
  @Input() stockRecords: IStockRecords | null = null;

  previousState(): void {
    window.history.back();
  }
}
