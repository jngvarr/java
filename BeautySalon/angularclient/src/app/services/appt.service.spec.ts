import { TestBed } from '@angular/core/testing';

import { ApptService } from './appt.service';

describe('ApptService', () => {
  let service: ApptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
