export interface AdminUser {
    id: number;
    name: string;
    email: string;
    role: string;
    enabled: boolean;
}

export interface AdminUpdateUserRequest {
    name: string;
    email: string;
    password: string;
}