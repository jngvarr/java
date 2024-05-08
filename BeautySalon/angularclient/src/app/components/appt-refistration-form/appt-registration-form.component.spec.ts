import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApptRegistrationFormComponent } from './appt-registration-form.component';

describe('ApptRefistrationFormComponent', () => {
  let component: ApptRegistrationFormComponent;
  let fixture: ComponentFixture<ApptRegistrationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApptRegistrationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ApptRegistrationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
