export interface Note {
    id?: number;
    title: string;
    content: string;
    referenceDate: string;
    hasAttachment?: boolean;
    attachmentFileName?: string;
    attachmentContentType?: string;
    attachmentSize?: number;
}


