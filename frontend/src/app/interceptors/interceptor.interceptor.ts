import {
  HttpEvent,
  HttpInterceptorFn,
  HttpResponse,
} from '@angular/common/http';
import { tap } from 'rxjs';

const iso8601DateOnly: RegExp = /^(\d{4})-(\d{2})-(\d{2})$/;
const iso8601FullDate: RegExp = /^(\d{4})-(\d{2})-(\d{2})/;

export const interceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    tap((event: HttpEvent<any>) => {
      if (event instanceof HttpResponse) {
        const body: any = event.body;
        convertToDate(body);
      }
    })
  );
};

function convertToDate(body: any): any {
  if (body === null || body === undefined) {
    return body;
  }

  if (typeof body !== 'object') {
    return body;
  }

  Object.keys(body).forEach((key: string) => {
    const value: any = body[key];

    if (isIso8601DateOnly(value)) {
      const date: any = new Date(value);
      date.setHours(6);
      body[key] = new Date(date);
    } else if (isIso86012FullDate(value)) {
      body[key] = new Date(value);
    } else if (typeof value === 'object') {
      convertToDate(value);
    } else if (typeof value === 'string' && tryJsonParseToObject(value)) {
      body[key] = JSON.parse(value);
      convertToDate(body[key]);
    }
  });
}

function isIso8601DateOnly(value?: string | null): boolean {
  if (value === null || value === undefined) {
    return false;
  }

  return iso8601DateOnly.test(value);
}

function isIso86012FullDate(value?: string | null): boolean {
  if (value === null || value === undefined) {
    return false;
  }

  return iso8601FullDate.test(value);
}

function tryJsonParseToObject(value: string): boolean {
  try {
    JSON.parse(value);
  } catch (e) {
    return false;
  }

  return true;
}

