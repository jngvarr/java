import {Injectable} from '@angular/core';
import {HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, switchMap, tap} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {Router} from "@angular/router";


@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  constructor(private http: HttpClient, private router: Router) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = sessionStorage.getItem("token")
    let authRequest = req;
    if (token) {
      authRequest = this.addToken(req, token);
    }
    return next.handle(authRequest)

    //   .pipe(
    //   catchError(error => {
    //     if (error instanceof HttpErrorResponse && error.status === 401) {
    //       // Перенаправляем пользователя на сервер для повторной аутентификации
    //       //TODO реализовать обновление accessToken
    //       this.router.navigate(['/login'], {
    //         queryParams: {
    //           returnUrl: req.url
    //         }
    //       });
    //     }
    //     return throwError(error);
    //   })
    // );
  }

  private addToken(req: HttpRequest<any>, token: string): HttpRequest<any> {
    const clonedRequest = req.clone({
      headers: req.headers
        .set('Authorization', `Bearer ${token}`)
        .set('Content-Type', 'application/json')
    });
    console.log('Cloned request headers:', clonedRequest.headers.keys());
    return clonedRequest;
  }
}
