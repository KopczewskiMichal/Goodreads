export interface Book {
  readonly bookId: number;
  readonly isbn: string;
  readonly title: string;
  readonly author: string;
  readonly releaseDate: Date;
  readonly description: string;
  readonly pucharseLink: string;
}