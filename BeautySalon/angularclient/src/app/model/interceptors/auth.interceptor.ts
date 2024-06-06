
import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';


@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  constructor(private http: HttpClient) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = sessionStorage.getItem('token');
    let authRequest = request;

    if (token) {
      authRequest = this.addToken(request, token);
    }

    return next.handle(authRequest).pipe(
      catchError(error => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          return this.handle401Error(authRequest, next);
        }
        return throwError(error);
      })
    );
  }

  private addToken(req: HttpRequest<any>, token: string): HttpRequest<any> {
    return req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`),
      // Добавляем заголовок Content-Type: application/json
      setHeaders: {
        'Content-Type': 'application/json'
      }
    });
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      return this.http.post<any>('/users/refresh', { token: sessionStorage.getItem('refreshToken') })
        .pipe(
          catchError(error => {
            this.isRefreshing = false;
            return throwError(error);
          }),
          catchError(() => {
            this.isRefreshing = false;
            return throwError('Refresh token failed');
          }),
          catchError(() => {
            return next.handle(req);
          })
        );
    } else {
      return next.handle(req);
    }
  }
}
