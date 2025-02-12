export interface Book {
  readonly bookId: number;
  readonly ISBN: string;
  readonly title: string;
  readonly author: string;
  readonly releaseDate: string;
  readonly description: string;
  readonly pucharseLink: string;
}