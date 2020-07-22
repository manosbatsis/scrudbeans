import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScrudBeansClientRestComponent } from './scrud-beans-client-rest.component';

describe('ScrudBeansClientRestComponent', () => {
  let component: ScrudBeansClientRestComponent;
  let fixture: ComponentFixture<ScrudBeansClientRestComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScrudBeansClientRestComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScrudBeansClientRestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
