import { TestBed } from '@angular/core/testing';

import { RefistrationService } from './refistration.service';

describe('RefistrationService', () => {
  let service: RefistrationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RefistrationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
