import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IStockRecords } from '../stock-records.model';
import { StockRecordsService } from '../service/stock-records.service';

@Component({
  standalone: true,
  templateUrl: './stock-records-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class StockRecordsDeleteDialogComponent {
  stockRecords?: IStockRecords;

  protected stockRecordsService = inject(StockRecordsService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.stockRecordsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
