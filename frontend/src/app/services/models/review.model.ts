export interface Review {
  readonly reviewId: number;
  readonly text: string;
  readonly createdAt: Date;
  readonly stars: number;
  readonly user: {
    username: string;
  };
}
