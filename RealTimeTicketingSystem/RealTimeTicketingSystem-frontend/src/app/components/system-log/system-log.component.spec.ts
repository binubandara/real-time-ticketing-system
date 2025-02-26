import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemLogComponent } from './system-log.component';

describe('SystemLogComponent', () => {
  let component: SystemLogComponent;
  let fixture: ComponentFixture<SystemLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SystemLogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SystemLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
