import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApptFormComponent } from './appt-form.component';

describe('ApptFormComponent', () => {
  let component: ApptFormComponent;
  let fixture: ComponentFixture<ApptFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApptFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ApptFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
