import { TestBed } from '@angular/core/testing';

import { ScrudBeansClientRestService } from './scrud-beans-client-rest.service';

describe('ScrudBeansClientRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ScrudBeansClientRestService = TestBed.get(ScrudBeansClientRestService);
    expect(service).toBeTruthy();
  });
});
