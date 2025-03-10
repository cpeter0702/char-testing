import { inject, NgModule } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateService, TranslateLoader, MissingTranslationHandler } from '@ngx-translate/core';
import { translatePartialLoader, missingTranslationHandler } from 'app/config/translation.config';
import { StateStorageService } from 'app/core/auth/state-storage.service';

@NgModule({
  imports: [
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: translatePartialLoader,
        deps: [HttpClient],
      },
      missingTranslationHandler: {
        provide: MissingTranslationHandler,
        useFactory: missingTranslationHandler,
      },
    }),
  ],
})
export class TranslationModule {
  private translateService = inject(TranslateService);
  private stateStorageService = inject(StateStorageService);

  constructor() {
    this.translateService.setDefaultLang('zh-tw');
    // if user have changed language and navigates away from the application and back to the application then use previously choosed language
    const langKey = this.stateStorageService.getLocale() ?? 'zh-tw';
    this.translateService.use(langKey);
  }
}
