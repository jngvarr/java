import { TestBed } from '@angular/core/testing';

import { ServiceForServices } from './service-for-services.service';

describe('ServiceForServicesService', () => {
  let service: ServiceForServices;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServiceForServices);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
