export interface UserDto {
  readonly id: number;
  readonly username: string;
  readonly email: string;
  readonly description: string;
  readonly password: string;
  readonly confirmPassword: string;
}
